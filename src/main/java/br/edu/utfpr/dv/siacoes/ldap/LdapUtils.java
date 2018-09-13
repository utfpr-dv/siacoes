package br.edu.utfpr.dv.siacoes.ldap;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.CommunicationException;
import javax.naming.directory.Attribute;

/**
 * Authenticates with LDAP Servers. Just using a single UID this class goes deep
 * inside the user's tree and find the full DN for the given UID. It also allows
 * to connect to servers when you don't have the certificate yet... but use this
 * feature at your own risk!
 * 
 * @author Tiago J. Adami (adamitj@gmail.com)
 *
 */
public class LdapUtils {
	public static final String ENTRY_SEPARATOR = ";;";
	private InitialDirContext ldap;
	private String host;
	private int port;
	private boolean useSSL;
	private boolean ignoreCertificates;
	private String basedn;
	private String varUid;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public boolean isIgnoreCertificates() {
		return ignoreCertificates;
	}

	public void setIgnoreCertificates(boolean ignoreCertificates) {
		this.ignoreCertificates = ignoreCertificates;
	}

	public String getBasedn() {
		return basedn;
	}

	public void setBasedn(String basedn) {
		this.basedn = basedn;
	}

	public String getVarUid() {
		return varUid;
	}

	public void setVarUid(String varUid) {
		this.varUid = varUid;
	}

	/**
	 * Default constructor
	 * 
	 * @param host
	 * @param port
	 * @param useSSL
	 * @param ignoreCertificates
	 * @param basedn
	 * @param varUid
	 */
	public LdapUtils(String host, int port, boolean useSSL, boolean ignoreCertificates, String basedn, String varUid) {
		super();
		this.host = host;
		this.port = port;
		this.useSSL = useSSL;
		this.ignoreCertificates = ignoreCertificates;
		this.basedn = basedn;
		this.varUid = varUid;

		System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", String.valueOf(ignoreCertificates));
	}

	/**
	 * Creates a new DirContext based on Ldap attribute values.
	 * 
	 * @return
	 * @throws NamingException
	 */
	private DirContext getDirContext() throws NamingException {
		String url = this.getUrl() + "/" + this.basedn;

		Hashtable<String, Object> env = createNoUserLdapProperties(url);

		DirContext ctx = null;
		ctx = new InitialDirContext(env);
		return ctx;
	}

	/**
	 * Creates a non user properties for LDAP connection
	 * 
	 * @param url
	 * @return
	 */
	private Hashtable<String, Object> createNoUserLdapProperties(String url) {
		Hashtable<String, Object> env = new Hashtable<String, Object>(11);

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);

		if (this.useSSL) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		if (this.useSSL && this.ignoreCertificates) {
			// env.put("java.naming.ldap.factory.socket",
			// "br.edu.utfpr.ldaptest.TrustAllCertificatesSSLSocketFactory");
			env.put("java.naming.ldap.factory.socket", TrustAllCertificatesSSLSocketFactory.class.getCanonicalName());
		}

		return env;
	}

	/**
	 * Authenticates an user and password from LDAP credentials;
	 * 
	 * @param uid
	 * @param password
	 * @return
	 * @throws NamingException
	 * @throws InterruptedException
	 */
	public void authenticate(String uid, String password) throws NamingException, InterruptedException {

		String url = getUrl();
		String dn = this.getDnByUid(uid);

		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);

		env.put(Context.SECURITY_AUTHENTICATION, "simple");

		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		// System.out.println(dn);

		if (this.useSSL) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		if (this.useSSL && this.ignoreCertificates) {
			env.put("java.naming.ldap.factory.socket", TrustAllCertificatesSSLSocketFactory.class.getCanonicalName());
		}

		ldap = new InitialDirContext(env);
		if (ldap != null)
			ldap.close();
	}

	/**
	 * Returns the url based on SSL or not
	 * 
	 * @return
	 */
	private String getUrl() {
		StringBuilder url = new StringBuilder();

		url.append(this.useSSL ? "ldaps://" : "ldap://");
		url.append(host);
		url.append(":");
		url.append(port);
		return url.toString();
	}

	/**
	 * Return LDAP authentication modes allowed by the server
	 * 
	 * @param url
	 * @return
	 * @throws NamingException
	 */
	public Attributes getLdapAuths() throws NamingException {

		// Create initial context
		DirContext ctx = new InitialDirContext();

		// Read supportedSASLMechanisms from root DSE
		Attributes attrs = ctx.getAttributes(this.getUrl(), new String[] { "supportedSASLMechanisms" });

		System.out.println(attrs);

		return attrs;

	}

	/**
	 * Returns all LDAP tree without attributes.
	 * 
	 * @param uidVar
	 *            the name of main unique identifier attribute. This is need for the
	 *            empty filter works.
	 * @return a list of all user entries within the LDAP Tree
	 * @throws NamingException
	 */
	public Map<String, String> getAllUsers(String uidVar) throws NamingException {
		Map<String, String> map = new HashMap<String, String>();

		NamingEnumeration<SearchResult> enumResult = null;
		DirContext dirContext = null;

		try {
			dirContext = this.getDirContext();

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String[] attrIDs = { uidVar };
			controls.setReturningAttributes(attrIDs);
			// enumResult =
			// dirContext.search("","(&(objectCategory=person)(objectClass=user)(CN=*))",
			// controls);
			enumResult = dirContext.search("", "(&(" + uidVar + "=*))", controls);

			while (enumResult.hasMore()) {
				SearchResult searchResult = (SearchResult) enumResult.next();
				Attributes attributes = searchResult.getAttributes();
				Attribute attr = attributes.get(uidVar);
				map.put((String) attr.get(), searchResult.getNameInNamespace());
			}

		} catch (NamingException e) {
			throw e;
		} finally {
			if (enumResult != null) {
				enumResult.close();
			}
			if (dirContext != null) {
				dirContext.close();
			}
		}

		return map;
	}

	/**
	 * Returns the full DN (distinct name) for a given UID
	 * 
	 * @param uid
	 *            the UID name of the user
	 * @return full tree path of LDAP
	 * @throws NamingException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public String getDnByUid(String uid) throws NamingException, InterruptedException {
		String url = this.getUrl() + "/" + this.basedn;

		Hashtable<String, Object> env = createNoUserLdapProperties(url);
		String ret = "uid=" + uid;
		DirContext ctx = null;

		try {
			// Create initial context
			// Try 3 times until give up
			for (int i = 0; i < 3; i++) {
				try {
					ctx = new InitialDirContext(env);
					break;
				} catch (CommunicationException ce) {
					// wait 5 secs before try again
					System.out.println("ERROR COMMUNICATING WITH LDAP. RETRY IN 5 SEC. STEP " + (i + 1) + " OF 3");
					Thread.sleep(5000);

					if (i + 1 == 3) {
						throw ce;
					} else {
						continue;
					}
				}
			}

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration answer = null;

			answer = ctx.search("", "(" + this.varUid.trim() + "=" + uid + ")", controls);

			while (answer.hasMore()) {
				SearchResult sr = (SearchResult) answer.next();
				ret = sr.getNameInNamespace();
				break;
			}

		} catch (CommunicationException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} catch (NamingException e) {
			throw e;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					// nothing
				}
			}
		}

		return ret;
	}

	/**
	 * Retrieves a list of OU from the baseDn.
	 * 
	 * @param uid
	 *            the Unique identifier
	 * @return a list of groups
	 * @throws NamingException
	 */
	public List<String> getLdapOuByUid(String uid, String baseDn) throws NamingException {
		List<String> ouList = new ArrayList<String>();
		String split[];

		baseDn = baseDn.trim().toUpperCase();

		split = baseDn.split(",");

		String group;

		for (String s : split) {
			if (s.substring(0, 3).equals("OU=")) {
				group = s.substring(s.indexOf("=") + 1);
				ouList.add(group);
			}
		}

		return ouList;
	}

	/**
	 * Retrieves all information of the user from LDAP Server
	 * 
	 * @param uid
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, String> getLdapProperties(String uid) throws NamingException {
		Map<String, String> mapa = new HashMap<String, String>();

		String url = this.getUrl() + "/" + this.basedn;

		Hashtable<String, Object> env = createNoUserLdapProperties(url);
		DirContext ctx = null;

		try {
			// Create initial context
			ctx = new InitialDirContext(env);

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration answer = ctx.search("", "(" + this.varUid + "=" + uid + ")", controls);

			while (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();
				Attributes attribs = result.getAttributes();
				// NamingEnumeration values = ((BasicAttribute)
				// attribs.get("distinguishedName")).getAll();
				NamingEnumeration values = attribs.getAll();

				while (values.hasMore()) {
					String attributeValue = values.next().toString();
					// System.out.println(attributeValue);
					String split[] = attributeValue.split(":");

					if (split.length == 2) {
						mapa.put(split[0].trim(), split[1].trim());
					}
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// Close the context when we're done
			ctx.close();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return mapa;
	}

	/**
	 * Retrieves all UID of all users from LDAP Server
	 * 
	 * @param uid
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getAllLdapUidInfo() throws NamingException {
		List<String> mapa = new ArrayList<String>();

		String url = this.getUrl() + "/" + this.basedn;

		Hashtable<String, Object> env = createNoUserLdapProperties(url);
		DirContext ctx = null;

		try {
			// Create initial context
			ctx = new InitialDirContext(env);

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration answer = ctx.search("", "(" + this.varUid + "=*)", controls);

			while (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration values = attribs.getAll();

				while (values.hasMore()) {
					String attributeValue = values.next().toString();
					String split[] = attributeValue.split(":");

					if (split.length == 2) {
						if (split[0].equals(varUid)) {
							mapa.add(split[1].trim());
						}
					}
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// Close the context when we're done
			ctx.close();
		}

		return mapa;
	}

	/**
	 * Returns all LDAP tree in a List<String>. Each position in list contains the
	 * entire entry attributes separated by the string <code>--;;</code>. A new
	 * entry is created when reaches <b>uidVar</b> attribute.
	 * 
	 * @param uidVar
	 *            The name of unique identifier attribute
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getAllLdapInfo(String uidVar) throws NamingException {
		List<String> mapa = new ArrayList<String>();

		String url = this.getUrl() + "/" + this.basedn;

		Hashtable<String, Object> env = createNoUserLdapProperties(url);
		DirContext ctx = null;

		try {
			// Create initial context
			ctx = new InitialDirContext(env);

			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration answer = ctx.search("", "(" + this.varUid + "=*)", controls);

			while (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();
				Attributes attribs = result.getAttributes();
				NamingEnumeration values = attribs.getAll();

				String entry = null;

				while (values.hasMore()) {
					String attributeValue = values.next().toString();

					if (entry == null) {
						entry = attributeValue;
					} else {
						entry = entry + ENTRY_SEPARATOR + attributeValue;
					}
				}

				if (entry != null && entry.trim().length() > 0) {
					mapa.add(entry);
				}
			}

		} catch (NamingException e) {
			e.printStackTrace();
			throw e;
		} finally {
			// Close the context when we're done
			ctx.close();
		}

		return mapa;
	}

}