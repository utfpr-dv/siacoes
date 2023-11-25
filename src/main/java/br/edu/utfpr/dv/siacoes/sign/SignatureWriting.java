package br.edu.utfpr.dv.siacoes.sign;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SignatureWriting {

	public enum SignFont{
		ALEXBRUSH(0), ARTYSIGNATURE(1), BIENCHEN(2), CEDARVILLECURSIVE(3), DARKWOMAN(4), DRAWINGOFANEWDAY(5), 
			DEUTSCHENORMALSCHRIFT(6), EVASDIGISCRIPT(7), FAREWELL(8), HERRVONMUELERHOFF(9), IMRANSSCHOOL(10), 
			KHAND(11), KRISTI(12), LABELLEAURORE(13), MAYQUEEN(14), OTTO(15), PECITA(16), SCRIPTINA(17);
		
		private final int value; 
		SignFont(int value) { 
			this.value = value; 
		}
		
		public int getValue() { 
			return value;
		}
		
		public static SignFont valueOf(int value) {
			for(SignFont d : SignFont.values()) {
				if(d.getValue() == value) {
					return d;
				}
			}
			
			return null;
		}
		
		public String toString() {
			return this.name();
		}
		
	}
	
	public static byte[] getSignature(String name, SignFont font) throws IOException, FontFormatException, URISyntaxException {
		return SignatureWriting.getSignature(name, font, false);
	}
	
	public static byte[] getSignature(String name, SignFont font, boolean strike) throws IOException, FontFormatException, URISyntaxException {
		JLabel label = new JLabel(name);
		
		label.setFont(SignatureWriting.getFont(font, strike));
		label.setForeground(java.awt.Color.BLACK);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		switch(font) {
			case ALEXBRUSH:
				label.setSize((int)label.getPreferredSize().getWidth() + 20, (int)label.getPreferredSize().getHeight());
				break;
			case ARTYSIGNATURE:
				label.setSize((int)label.getPreferredSize().getWidth() + 20, (int)label.getPreferredSize().getHeight());
				break;
			case BIENCHEN:
				label.setSize((int)label.getPreferredSize().getWidth() + 20, (int)label.getPreferredSize().getHeight());
				break;
			case CEDARVILLECURSIVE:
				label.setSize((int)label.getPreferredSize().getWidth() + 10, (int)label.getPreferredSize().getHeight());
				break;
			case DARKWOMAN:
				label.setSize((int)label.getPreferredSize().getWidth() + 10, (int)label.getPreferredSize().getHeight());
				break;
			case DEUTSCHENORMALSCHRIFT:
				label.setSize((int)label.getPreferredSize().getWidth() + 20, (int)label.getPreferredSize().getHeight());
				break;
			case FAREWELL:
				label.setSize((int)label.getPreferredSize().getWidth() + 30, (int)label.getPreferredSize().getHeight());
				break;
			case HERRVONMUELERHOFF:
				label.setSize((int)label.getPreferredSize().getWidth() + 50, (int)label.getPreferredSize().getHeight());
				break;
			case IMRANSSCHOOL:
				label.setSize((int)label.getPreferredSize().getWidth() + 10, (int)label.getPreferredSize().getHeight());
				break;
			case KRISTI:
				label.setSize((int)label.getPreferredSize().getWidth() + 100, (int)label.getPreferredSize().getHeight());
				break;
			case LABELLEAURORE:
				label.setSize((int)label.getPreferredSize().getWidth() + 10, (int)label.getPreferredSize().getHeight());
				break;
			case MAYQUEEN:
				label.setSize((int)label.getPreferredSize().getWidth() + 30, (int)label.getPreferredSize().getHeight());
				break;
			case OTTO:
				label.setSize((int)label.getPreferredSize().getWidth() + 20, (int)label.getPreferredSize().getHeight());
				break;
			case PECITA:
				label.setSize((int)label.getPreferredSize().getWidth() + 150, (int)label.getPreferredSize().getHeight());
				break;
			case SCRIPTINA:
				label.setSize((int)label.getPreferredSize().getWidth() + 100, (int)label.getPreferredSize().getHeight());
				break;
			default:
				label.setSize(label.getPreferredSize());
		}
		
		BufferedImage img = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		label.printAll(g2d);
		g2d.dispose();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);
		out.flush();
		
		return out.toByteArray();
	}
	
	private static Font getFont(SignFont font, boolean strike) throws FontFormatException, IOException, URISyntaxException {
		Font f = null;
		
		switch(font) {
			case ALEXBRUSH:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/AlexBrush/alexbrush.ttf"));
				break;
			case ARTYSIGNATURE:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/ArtySignature/artysignature.otf"));
				break;
			case BIENCHEN:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Bienchen/bienchen.ttf"));
				break;
			case CEDARVILLECURSIVE:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/CedarvilleCursive/cedarvillecursive.ttf"));
				break;
			case DARKWOMAN:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/DarkWoman/darkwoman.otf"));
				break;
			case DRAWINGOFANEWDAY:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/DrawingOfANewDay/drawingofanewday.ttf"));
				break;
			case DEUTSCHENORMALSCHRIFT:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/DeutscheNormalschrift/deutschenormalschrift.ttf"));
				break;
			case EVASDIGISCRIPT:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/EvasdigiScript/evasdigiscript.ttf"));
				break;
			case FAREWELL:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Farewell/farewell.ttf"));
				break;
			case HERRVONMUELERHOFF:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/HerrVonMuellerhoff/herrvonmuellerhoff.ttf"));
				break;
			case IMRANSSCHOOL:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/ImransSchool/imransschool.ttf"));
				break;
			case KHAND:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Khand/khand.ttf"));
				break;
			case KRISTI:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Kristi/kristi.ttf"));
				break;
			case LABELLEAURORE:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/LaBelleAurore/labelleaurore.ttf"));
				break;
			case MAYQUEEN:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/MayQueen/mayqueen.ttf"));
				break;
			case OTTO:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Otto/otto.ttf"));
				break;
			case PECITA:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Pecita/pecita.otf"));
				break;
			case SCRIPTINA:
				f = Font.createFont(Font.TRUETYPE_FONT, SignatureWriting.class.getResourceAsStream("/fonts/Scriptina/scriptin.ttf"));
				break;
		}
		
		f = f.deriveFont(110.0F);
		
		if(strike) {
			Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	
			f = f.deriveFont(attributes);
		}
		
		return f;
	}
	
}
