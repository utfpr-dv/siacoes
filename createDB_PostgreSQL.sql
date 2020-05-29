CREATE TABLE appconfig (
    theme SMALLINT NOT NULL,
	host VARCHAR(255) NOT NULL,
	sigacenabled SMALLINT NOT NULL,
	sigesenabled SMALLINT NOT NULL,
	sigetenabled SMALLINT NOT NULL,
	mobileenabled SMALLINT NOT NULL
);

CREATE TABLE ldapconfig (
  idldapconfig SERIAL NOT NULL ,
  host VARCHAR(100) NOT NULL ,
  port INT NOT NULL ,
  useSSL SMALLINT NOT NULL ,
  ignoreCertificates SMALLINT NOT NULL ,
  basedn VARCHAR(100) NOT NULL ,
  uidField VARCHAR(100) NOT NULL ,
  cpfField VARCHAR(100) NOT NULL ,
  registerField VARCHAR(100) NOT NULL ,
  nameField VARCHAR(100) NOT NULL ,
  emailField VARCHAR(100) NOT NULL ,
  PRIMARY KEY (idldapconfig)
);

CREATE TABLE emailconfig (
  idemailconfig SERIAL NOT NULL,
  host VARCHAR(255) NOT NULL,
  "user" VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  port INT NOT NULL,
  enableSsl SMALLINT NOT NULL,
  authenticate SMALLINT NOT NULL,
  signature TEXT NOT NULL,
  PRIMARY KEY (idemailconfig)
);

CREATE TABLE emailmessage (
  idemailmessage SERIAL NOT NULL,
  subject VARCHAR(255) NOT NULL,
  message text NOT NULL,
  datafields text NOT NULL,
  module SMALLINT NOT NULL,
  PRIMARY KEY (idemailmessage)
);

CREATE TABLE campus (
  idcampus SERIAL NOT NULL,
  name VARCHAR(100) NOT NULL,
  initials VARCHAR(50) NOT NULL,
  address VARCHAR(100) NOT NULL,
  site VARCHAR(255) NOT NULL,
  logo BYTEA,
  active SMALLINT NOT NULL,
  PRIMARY KEY (idcampus)
);

CREATE TABLE department (
  iddepartment SERIAL NOT NULL,
  idcampus INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  initials VARCHAR(50) NOT NULL,
  fullName VARCHAR(255) NOT NULL,
  site VARCHAR(255) NOT NULL,
  logo BYTEA,
  active SMALLINT NOT NULL,
  PRIMARY KEY (iddepartment),
  CONSTRAINT fk_department_campus FOREIGN KEY (idcampus) REFERENCES campus (idcampus) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_department_campus_idx ON department (idcampus);

CREATE TABLE semester (
    idcampus INT NOT NULL,
    semester SMALLINT NOT NULL,
    year INT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    CONSTRAINT semester_pkey PRIMARY KEY (idcampus, semester, year),
    CONSTRAINT fk_semester_idcampus FOREIGN KEY (idcampus) REFERENCES campus (idcampus) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE  TABLE sigacconfig (
  iddepartment INT NOT NULL ,
  minimumScore REAL NOT NULL ,
  maxfilesize INT NOT NULL ,
  usedigitalsignature smallint NOT NULL ,
  PRIMARY KEY (iddepartment) ,
  CONSTRAINT fk_sigacconfig_iddepartment FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE  TABLE sigesconfig (
  iddepartment INT NOT NULL ,
  minimumScore REAL NOT NULL ,
  supervisorPonderosity REAL NOT NULL ,
  companySupervisorPonderosity REAL NOT NULL ,
  showgradestostudent SMALLINT NOT NULL ,
  supervisorfilter SMALLINT NOT NULL ,
  supervisorFillJuryForm SMALLINT NOT NULL ,
  maxfilesize INT NOT NULL ,
  jurytime INT NOT NULL ,
  fillonlytotalhours SMALLINT NOT NULL ,
  juryformat SMALLINT NOT NULL ,
  appraiserfillsgrades smallint NOT NULL ,
  usedigitalsignature smallint NOT NULL ,
  minimumjurymembers SMALLINT NOT NULL ,
  minimumjurysubstitutes SMALLINT NOT NULL ,
  PRIMARY KEY (iddepartment) ,
  CONSTRAINT fk_sigesconfig_iddepartment FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE  TABLE sigetconfig (
  iddepartment INT NOT NULL ,
  minimumScore REAL NOT NULL ,
  registerProposal SMALLINT NOT NULL ,
  showgradestostudent SMALLINT NOT NULL ,
  supervisorfilter SMALLINT NOT NULL ,
  cosupervisorfilter SMALLINT NOT NULL ,
  supervisorindication SMALLINT NOT NULL ,
  maxtutoredstage1 SMALLINT NOT NULL ,
  maxtutoredstage2 SMALLINT NOT NULL ,
  requestfinaldocumentstage1 SMALLINT NOT NULL ,
  repositorylink VARCHAR(255) NOT NULL ,
  supervisorjuryrequest SMALLINT NOT NULL ,
  supervisoragreement SMALLINT NOT NULL ,
  supervisorjuryagreement SMALLINT NOT NULL ,
  validateattendances SMALLINT NOT NULL ,
  attendancefrequency SMALLINT NOT NULL ,
  maxfilesize INT NOT NULL ,
  minimumjurymembers SMALLINT NOT NULL ,
  minimumjurysubstitutes SMALLINT NOT NULL ,
  jurytimestage1 INT NOT NULL ,
  jurytimestage2 INT NOT NULL ,
  supervisorassignsgrades SMALLINT NOT NULL ,
  appraiserfillsgrades smallint NOT NULL ,
  usedigitalsignature smallint NOT NULL ,
  PRIMARY KEY (iddepartment) ,
  CONSTRAINT fk_sigetconfig_iddepartment FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE country (
  idcountry SERIAL NOT NULL,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (idcountry)
);

CREATE TABLE state (
  idstate SERIAL NOT NULL,
  idcountry INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  initials VARCHAR(10) NOT NULL,
  PRIMARY KEY (idstate),
  CONSTRAINT fk_state_country FOREIGN KEY (idcountry) REFERENCES country (idcountry) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_state_country_idx ON state (idcountry);

CREATE TABLE city (
  idcity SERIAL NOT NULL,
  idstate INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  PRIMARY KEY (idcity),
  CONSTRAINT fk_city_state FOREIGN KEY (idstate) REFERENCES state (idstate) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_city_state_idx ON city (idstate);

CREATE TABLE company (
  idcompany SERIAL NOT NULL,
  idcity INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  cnpj VARCHAR(20) NOT NULL,
  phone VARCHAR(45) NOT NULL,
  email VARCHAR(100) NOT NULL,
  agreement VARCHAR(50) NOT NULL,
  PRIMARY KEY (idcompany),
  CONSTRAINT fk_company_city FOREIGN KEY (idcity) REFERENCES city (idcity) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_company_city_idx ON company (idcity);

CREATE TABLE "user" (
  iduser SERIAL NOT NULL,
  idDepartment INT DEFAULT NULL,
  idcompany INT NULL,
  name VARCHAR(255) NOT NULL,
  login VARCHAR(50) NOT NULL,
  password VARCHAR(500) NOT NULL,
  salt VARCHAR(255) NOT NULL,
  email VARCHAR(100) NOT NULL,
  phone VARCHAR(100) NOT NULL,
  institution VARCHAR(100) NOT NULL,
  research text NOT NULL,
  external SMALLINT NOT NULL,
  lattes VARCHAR(100) NOT NULL,
  active SMALLINT NOT NULL,
  area VARCHAR(100) NOT NULL,
  studentCode VARCHAR(45) NOT NULL,
  photo BYTEA DEFAULT NULL,
  PRIMARY KEY (iduser),
  CONSTRAINT fk_user_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_user_company FOREIGN KEY (idcompany ) REFERENCES company (idcompany ) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_user_department_idx ON "user" (idDepartment);
CREATE INDEX fk_user_company_idx ON "user" (idcompany);

CREATE TABLE userprofile (
    iduser integer NOT NULL,
    profile smallint NOT NULL,
    PRIMARY KEY (iduser, profile),
    CONSTRAINT fk_userprofile_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_userprofile_user_idx ON userprofile (iduser);

CREATE TABLE userdepartment (
    iduserdepartment serial NOT NULL,
    iduser integer NOT NULL,
    iddepartment integer NOT NULL,
    profile smallint NOT NULL,
    sigacmanager smallint NOT NULL,
    sigesmanager smallint NOT NULL,
    sigetmanager smallint NOT NULL,
    departmentmanager smallint NOT NULL,
    registersemester smallint NOT NULL,
    registeryear integer NOT NULL,
    PRIMARY KEY (iduserdepartment),
    CONSTRAINT fk_userdepartment_iduser FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_userdepartment_iddepartment FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_userdepartment_iduser_idx ON userdepartment (iduser);
CREATE INDEX fk_userdepartment_iddepartment_idx ON userdepartment (iddepartment);

CREATE TABLE certificate (
  idcertificate SERIAL NOT NULL,
  iddepartment INT NOT NULL,
  iduser INT NOT NULL,
  module SMALLINT NOT NULL,
  "date" TIMESTAMP NOT NULL,
  guid VARCHAR(100) NOT NULL,
  file BYTEA NOT NULL,
  PRIMARY KEY (idcertificate),
  CONSTRAINT fk_certificate_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_certificate_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_certificate_department_idx ON certificate (iddepartment);
CREATE INDEX fk_certificate_user_idx ON certificate (iduser);

CREATE TABLE themesuggestion (
  idthemesuggestion SERIAL NOT NULL,
  idDepartment INT NOT NULL,
  idUser INT NULL,
  title VARCHAR(255) NOT NULL,
  proponent VARCHAR(100) NOT NULL,
  objectives text NOT NULL,
  proposal text NOT NULL,
  submissiondate date NOT NULL,
  active SMALLINT NOT NULL,
  PRIMARY KEY (idthemesuggestion),
  CONSTRAINT fk_themesuggestion_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_themesuggestion_user FOREIGN KEY (idUser ) REFERENCES "user" (iduser ) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_themesuggestion_department_idx ON themesuggestion (iddepartment);
CREATE INDEX fk_themesuggestion_user_idx ON themesuggestion (iduser);

CREATE TABLE internship (
  idinternship SERIAL NOT NULL,
  iddepartment INT NOT NULL,
  idcompany INT NOT NULL,
  idcompanysupervisor INT NOT NULL,
  idsupervisor INT NOT NULL,
  idstudent INT NOT NULL,
  type SMALLINT NOT NULL,
  requiredtype SMALLINT NOT NULL,
  comments text NOT NULL,
  reportTitle VARCHAR(255) NOT NULL,
  term VARCHAR(100) NOT NULL,
  startDate date NOT NULL,
  endDate date DEFAULT NULL,
  weekhours real NOT NULL,
  weekdays SMALLINT NOT NULL,
  totalHours INT NOT NULL,
  internshipPlan BYTEA NOT NULL,
  finalReport BYTEA,
  fillonlytotalhours SMALLINT NOT NULL,
  PRIMARY KEY (idinternship),
  CONSTRAINT fk_internship_company FOREIGN KEY (idcompany) REFERENCES company (idcompany) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internship_companysupervisor FOREIGN KEY (idcompanysupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internship_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internship_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internship_supervisor FOREIGN KEY (idsupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internship_company_idx ON internship (idcompany);
CREATE INDEX fk_internship_companysupervisor_idx ON internship (idcompanysupervisor);
CREATE INDEX fk_internship_supervisor_idx ON internship (idsupervisor);
CREATE INDEX fk_internship_student_idx ON internship (idstudent);
CREATE INDEX fk_internship_department_idx ON internship (iddepartment);

CREATE TABLE internshipreport (
  idinternshipreport SERIAL NOT NULL,
  idinternship INT NOT NULL,
  report BYTEA NOT NULL,
  type SMALLINT NOT NULL,
  date date NOT NULL,
  finalreport SMALLINT NOT NULL,
  feedback SMALLINT NOT NULL,
  feedbackdate TIMESTAMP,
  idfeedbackuser INT,
  PRIMARY KEY (idinternshipreport),
  CONSTRAINT fk_internshipreport_internship FOREIGN KEY (idinternship) REFERENCES internship (idinternship) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internshipreport_feedbackuser FOREIGN KEY (idfeedbackuser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_internshipreport_internship_idx ON internshipreport (idinternship);

CREATE  TABLE internshipevaluationitem (
  idinternshipevaluationitem SERIAL NOT NULL,
  iddepartment INT NOT NULL ,
  description VARCHAR(255) NOT NULL ,
  ponderosity REAL NOT NULL ,
  active SMALLINT NOT NULL ,
  sequence SMALLINT NOT NULL ,
  type SMALLINT NOT NULL ,
  PRIMARY KEY (idinternshipevaluationitem) ,
  CONSTRAINT fk_internshipevaluationitem_iddepartment FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipevaluationitem_iddepartment_idx ON internshipevaluationitem (iddepartment);

CREATE  TABLE internshipjury (
  idinternshipjury SERIAL NOT NULL,
  idinternship INT NOT NULL ,
  date TIMESTAMP NOT NULL ,
  local VARCHAR(100) NOT NULL ,
  comments TEXT NOT NULL ,
  startTime TIME NOT NULL ,
  endTime TIME NOT NULL ,
  minimumScore REAL NOT NULL ,
  supervisorPonderosity REAL NOT NULL ,
  companySupervisorPonderosity REAL NOT NULL ,
  companySupervisorScore REAL NOT NULL ,
  result smallint NOT NULL,
  supervisorAbsenceReason TEXT NOT NULL ,
  supervisorScore REAL NOT NULL ,
  supervisorFillJuryForm SMALLINT NOT NULL ,
  juryformat SMALLINT NOT NULL ,
  PRIMARY KEY (idinternshipjury) ,
  CONSTRAINT fk_internshipjury_idinternship FOREIGN KEY (idinternship) REFERENCES internship (idinternship) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipjury_idinternship_idx ON internshipjury (idinternship);

CREATE  TABLE internshipjuryappraiser (
  idinternshipjuryappraiser SERIAL NOT NULL,
  idinternshipjury INT NOT NULL ,
  idappraiser INT NOT NULL ,
  file BYTEA NULL ,
  additionalfile BYTEA NULL ,
  comments TEXT NOT NULL ,
  substitute SMALLINT NOT NULL ,
  chair SMALLINT NOT NULL ,
  PRIMARY KEY (idinternshipjuryappraiser) ,
  CONSTRAINT fk_internshipjuryappraiser_idinternshipjury FOREIGN KEY (idinternshipjury) REFERENCES internshipjury (idinternshipjury) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internshipjuryappraiser_idappraiser FOREIGN KEY (idappraiser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipjuryappraiser_idinternshipjury_idx ON internshipjuryappraiser (idinternshipjury);
CREATE INDEX fk_internshipjuryappraiser_idappraiser_idx ON internshipjuryappraiser (idappraiser);

CREATE  TABLE internshipjuryappraiserscore (
  idinternshipjuryappraiserscore SERIAL NOT NULL,
  idinternshipJuryAppraiser INT NOT NULL ,
  idinternshipEvaluationItem INT NOT NULL ,
  score REAL NOT NULL ,
  PRIMARY KEY (idinternshipjuryappraiserscore) ,
  CONSTRAINT fk_internshipjuryappraiserscore_idinternshipjuryappraiser FOREIGN KEY (idinternshipJuryAppraiser) REFERENCES internshipjuryappraiser (idinternshipjuryappraiser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internshipjuryappraiserscore_idinternshipevaluationitem FOREIGN KEY (idinternshipEvaluationItem) REFERENCES internshipevaluationitem (idinternshipevaluationitem) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipjuryappraiserscore_idinternshipjuryappraiser_idx ON internshipjuryappraiserscore (idinternshipJuryAppraiser);
CREATE INDEX fk_internshipjuryappraiserscore_idinternshipevaluationitem_idx ON internshipjuryappraiserscore (idinternshipEvaluationItem);

CREATE  TABLE internshipjurystudent (
  idinternshipjurystudent SERIAL NOT NULL,
  idinternshipJury INT NOT NULL ,
  idStudent INT NOT NULL ,
  PRIMARY KEY (idinternshipjurystudent) ,
  CONSTRAINT fk_internshipjurystudent_idinternshipjury FOREIGN KEY (idinternshipJury) REFERENCES internshipjury (idinternshipjury) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_internshipjurystudent_idstudent FOREIGN KEY (idStudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipjurystudent_idinternshipjury_idx ON internshipjurystudent (idinternshipJury);
CREATE INDEX fk_internshipjurystudent_idstudent_idx ON internshipjurystudent (idStudent);

CREATE TABLE internshipfinaldocument (
  idinternshipfinaldocument SERIAL NOT NULL,
  idinternship INT DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  submissionDate DATE NOT NULL,
  file BYTEA NOT NULL,
  private SMALLINT NOT NULL,
  supervisorFeedbackDate DATE NULL,
  supervisorFeedback SMALLINT NOT NULL,
  comments TEXT NOT NULL,
  PRIMARY KEY (idinternshipfinaldocument),
  CONSTRAINT fk_internshipfinaldocument_idinternship FOREIGN KEY (idinternship) REFERENCES internship (idinternship) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_internshipfinaldocument_idinternship_idx ON internshipfinaldocument (idinternship);

CREATE TABLE activityunit (
  idactivityunit SERIAL NOT NULL,
  description VARCHAR(50) NOT NULL,
  fillAmount SMALLINT NOT NULL,
  amountDescription VARCHAR(50) NOT NULL,
  PRIMARY KEY (idactivityunit)
);

CREATE TABLE activitygroup (
  idactivitygroup SERIAL NOT NULL,
  description VARCHAR(255) NOT NULL,
  sequence INT NOT NULL,
  minimumScore INT NOT NULL,
  maximumScore INT NOT NULL,
  PRIMARY KEY (idactivitygroup)
);

CREATE TABLE activity (
  idactivity SERIAL NOT NULL,
  idactivitygroup INT NOT NULL,
  idactivityunit INT NOT NULL,
  iddepartment INT NOT NULL,
  description VARCHAR(255) NOT NULL,
  score REAL NOT NULL,
  maximumInSemester REAL NOT NULL,
  active SMALLINT NOT NULL,
  sequence INT NOT NULL,
  PRIMARY KEY (idactivity),
  CONSTRAINT fk_activity_activitygroup FOREIGN KEY (idactivitygroup) REFERENCES activitygroup (idactivitygroup) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_activity_activityunit FOREIGN KEY (idactivityunit) REFERENCES activityunit (idactivityunit) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_activity_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_activity_activitygroup_idx ON activity (idactivitygroup);
CREATE INDEX fk_activity_activityunit_idx ON activity (idactivityunit);
CREATE INDEX fk_activity_department_idx ON activity (iddepartment);

CREATE TABLE activitysubmission (
  idactivitysubmission SERIAL NOT NULL,
  idStudent INT NOT NULL,
  idfeedbackuser INT NULL,
  iddepartment INT NOT NULL,
  idActivity INT NOT NULL,
  semester SMALLINT NOT NULL,
  year SMALLINT NOT NULL,
  submissionDate date NOT NULL,
  file BYTEA NOT NULL,
  amount REAL NOT NULL,
  feedback SMALLINT NOT NULL,
  feedbackDate date DEFAULT NULL,
  validatedAmount REAL NOT NULL,
  comments TEXT NOT NULL,
  description VARCHAR(100) NOT NULL,
  feedbackreason TEXT NOT NULL,
  PRIMARY KEY (idactivitysubmission),
  CONSTRAINT fk_activitysubmission_activity FOREIGN KEY (idActivity) REFERENCES activity (idactivity) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_activitysubmission_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_activitysubmission_student FOREIGN KEY (idStudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_activitysubmission_feedbackuser FOREIGN KEY (idfeedbackuser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_activitysubmission_student_idx ON activitysubmission (idStudent);
CREATE INDEX fk_activitysubmission_department_idx ON activitysubmission (iddepartment);
CREATE INDEX fk_activitysubmission_activity_idx ON activitysubmission (idActivity);
CREATE INDEX fk_activitysubmission_feedbackuser_idx ON activitysubmission (idfeedbackuser);

CREATE TABLE finalsubmission (
    idfinalsubmission SERIAL NOT NULL,
    iddepartment INT NOT NULL,
    idstudent INT NOT NULL,
    idfeedbackuser INT NOT NULL,
    date DATE NOT NULL,
	finalscore REAL NOT NULL,
    report BYTEA NOT NULL,
    PRIMARY KEY (idfinalsubmission),
    CONSTRAINT fk_finalsubmission_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_finalsubmission_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_finalsubmission_feedbackuser FOREIGN KEY (idfeedbackuser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_finalsubmission_department_idx ON finalsubmission (iddepartment);
CREATE INDEX fk_finalsubmission_student_idx ON finalsubmission (idStudent);
CREATE INDEX fk_finalsubmission_feedbackuser_idx ON finalsubmission (idfeedbackuser);

CREATE TABLE document (
  iddocument SERIAL NOT NULL,
  idDepartment INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  type SMALLINT NOT NULL,
  sequence INT NOT NULL,
  file BYTEA NOT NULL,
  module SMALLINT NOT NULL,
  PRIMARY KEY (iddocument),
  CONSTRAINT fk_document_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_document_department_idx ON document (idDepartment);

CREATE TABLE deadline (
  iddeadline SERIAL NOT NULL,
  idDepartment INT NOT NULL,
  semester SMALLINT NOT NULL,
  year SMALLINT NOT NULL,
  proposalDeadline date NOT NULL,
  projectDeadline date NOT NULL,
  thesisDeadline date NOT NULL,
  PRIMARY KEY (iddeadline),
  CONSTRAINT fk_deadline_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_deadline_department_idx ON deadline (idDepartment);

CREATE TABLE proposal (
  idproposal SERIAL NOT NULL,
  idDepartment INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  subarea VARCHAR(255) NOT NULL,
  idStudent INT NOT NULL,
  idSupervisor INT NOT NULL,
  idCoSupervisor INT DEFAULT NULL,
  file BYTEA DEFAULT NULL,
  semester SMALLINT NOT NULL,
  year SMALLINT NOT NULL,
  submissionDate date NOT NULL,
  invalidated SMALLINT NOT NULL,
  supervisorfeedback SMALLINT NOT NULL,
  supervisorfeedbackdate date DEFAULT NULL,
  supervisorcomments text NOT NULL,
  PRIMARY KEY (idproposal),
  CONSTRAINT fk_proposal_cosupervisor FOREIGN KEY (idCoSupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_proposal_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_proposal_student FOREIGN KEY (idStudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_proposal_supervisor FOREIGN KEY (idSupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_proposal_student_idx ON proposal (idStudent);
CREATE INDEX fk_proposal_advisor_idx ON proposal (idSupervisor);
CREATE INDEX fk_proposal_cosupervisor_idx ON proposal (idCoSupervisor);
CREATE INDEX fk_proposal_department_idx ON proposal (idDepartment);

CREATE TABLE proposalappraiser (
  idproposalAppraiser SERIAL NOT NULL,
  idProposal INT NOT NULL,
  idAppraiser INT NOT NULL,
  feedback SMALLINT NOT NULL,
  comments text NOT NULL,
  allowEditing SMALLINT NOT NULL,
  supervisorindication SMALLINT NOT NULL ,
  file BYTEA DEFAULT NULL,
  PRIMARY KEY (idproposalAppraiser),
  CONSTRAINT fk_proposalappraiser_appraiser FOREIGN KEY (idAppraiser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_proposalappraiser_proposal FOREIGN KEY (idProposal) REFERENCES proposal (idproposal) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_proposalappraiser_proposal_idx ON proposalappraiser (idProposal);
CREATE INDEX fk_proposalappraiser_appraiser_idx ON proposalappraiser (idAppraiser);

CREATE TABLE attendancegroup (
    idgroup serial NOT NULL,
    idproposal integer NOT NULL,
    idsupervisor integer NOT NULL,
    stage smallint NOT NULL,
    PRIMARY KEY (idgroup),
    CONSTRAINT fk_attendancegroup_proposal FOREIGN KEY (idproposal) REFERENCES proposal (idproposal) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_attendancegroup_supervisor FOREIGN KEY (idsupervisor) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_attendancegroup_proposal_idx ON attendancegroup (idproposal);
CREATE INDEX fk_attendancegroup_supervisor_idx ON attendancegroup (idsupervisor);

CREATE TABLE attendance (
  idattendance SERIAL NOT NULL,
  idproposal INT NOT NULL,
  idstudent INT NOT NULL,
  idsupervisor INT NOT NULL,
  idgroup INT,
  date date NOT NULL,
  startTime time NOT NULL,
  endTime time NOT NULL,
  comments text NOT NULL,
  nextMeeting text NOT NULL,
  stage INT NOT NULL,
  PRIMARY KEY (idattendance),
  CONSTRAINT fk_attendance_proposal FOREIGN KEY (idproposal) REFERENCES proposal (idproposal) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_attendance_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_attendance_supervisor FOREIGN KEY (idsupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_attendance_proposal_idx ON attendance (idProposal);
CREATE INDEX fk_attendance_student_idx ON attendance (idstudent);
CREATE INDEX fk_attendance_supervisor_idx ON attendance (idsupervisor);
CREATE INDEX fk_attendance_group_idx ON attendance (idgroup);

CREATE TABLE supervisorchange (
  idsupervisorchange SERIAL NOT NULL,
  idProposal INT NOT NULL,
  idOldSupervisor INT NOT NULL,
  idNewSupervisor INT NOT NULL,
  idOldCosupervisor INT DEFAULT NULL,
  idNewCosupervisor INT DEFAULT NULL,
  date TIMESTAMP NOT NULL,
  comments VARCHAR(255) NOT NULL,
  approved SMALLINT NOT NULL,
  approvalDate TIMESTAMP DEFAULT NULL,
  approvalComments VARCHAR(255) NOT NULL,
  supervisorrequest smallint NOT NULL,
  PRIMARY KEY (idsupervisorchange),
  CONSTRAINT fk_supervisorchange_newcosupervisor FOREIGN KEY (idNewCosupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_supervisorchange_newsupervisor FOREIGN KEY (idNewSupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_supervisorchange_oldcosupervisor FOREIGN KEY (idOldCosupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_supervisorchange_oldsupervisor FOREIGN KEY (idOldSupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_supervisorchange_proposal FOREIGN KEY (idProposal) REFERENCES proposal (idproposal) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_supervisorchange_oldsupervisor_idx ON supervisorchange (idOldSupervisor);
CREATE INDEX fk_supervisorchange_newsupervisor_idx ON supervisorchange (idNewSupervisor);
CREATE INDEX fk_supervisorchange_oldcosupervisor_idx ON supervisorchange (idOldCosupervisor);
CREATE INDEX fk_supervisorchange_newcosupervisor_idx ON supervisorchange (idNewCosupervisor);
CREATE INDEX fk_supervisorchange_proposal_idx ON supervisorchange (idProposal);

CREATE TABLE project (
  idproject SERIAL NOT NULL,
  idproposal INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  subarea VARCHAR(255) NOT NULL,
  idstudent INT NOT NULL,
  idsupervisor INT NOT NULL,
  file BYTEA NOT NULL,
  semester SMALLINT NOT NULL,
  year SMALLINT NOT NULL,
  submissiondate date NOT NULL,
  idcosupervisor INT DEFAULT NULL,
  abstract text NOT NULL,
  PRIMARY KEY (idproject),
  CONSTRAINT fk_project_cosupervisor FOREIGN KEY (idcosupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_project_proposal FOREIGN KEY (idproposal) REFERENCES proposal (idproposal) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_project_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_project_supervisor FOREIGN KEY (idsupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_project_proposal_idx ON project (idproposal);
CREATE INDEX fk_project_student_idx ON project (idstudent);
CREATE INDEX fk_project_supervisor_idx ON project (idsupervisor);
CREATE INDEX fk_project_cosupervisor_idx ON project (idcosupervisor);

CREATE TABLE thesis (
  idthesis SERIAL NOT NULL,
  idproject INT NOT NULL,
  title VARCHAR(255) NOT NULL,
  subarea VARCHAR(255) NOT NULL,
  idstudent INT NOT NULL,
  idsupervisor INT NOT NULL,
  file BYTEA NOT NULL,
  semester SMALLINT NOT NULL,
  year SMALLINT NOT NULL,
  submissiondate date NOT NULL,
  idcosupervisor INT DEFAULT NULL,
  abstract text NOT NULL,
  PRIMARY KEY (idthesis),
  CONSTRAINT fk_thesis_cosupervisor FOREIGN KEY (idcosupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_thesis_project FOREIGN KEY (idproject) REFERENCES project (idproject) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_thesis_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_thesis_supervisor FOREIGN KEY (idsupervisor) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_thesis_project_idx ON thesis (idproject);
CREATE INDEX fk_thesis_student_idx ON thesis (idstudent);
CREATE INDEX fk_thesis_supervisor_idx ON thesis (idsupervisor);
CREATE INDEX fk_thesis_cosupervisor_idx ON thesis (idcosupervisor);

CREATE TABLE finaldocument (
  idfinaldocument SERIAL NOT NULL,
  idProject INT DEFAULT NULL,
  idThesis INT DEFAULT NULL,
  title VARCHAR(255) NOT NULL,
  submissionDate date NOT NULL,
  file BYTEA NOT NULL,
  private SMALLINT NOT NULL,
  companyInfo SMALLINT NOT NULL,
  patent SMALLINT NOT NULL,
  supervisorFeedbackDate DATE NULL,
  supervisorFeedback SMALLINT NOT NULL,
  comments TEXT NOT NULL,
  abstract TEXT NOT NULL,
  abstract2 TEXT NOT NULL,
  PRIMARY KEY (idfinaldocument),
  CONSTRAINT fk_finaldocument_idproject FOREIGN KEY (idProject) REFERENCES project (idproject) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_finaldocument_idthesis FOREIGN KEY (idThesis) REFERENCES thesis (idthesis) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_finaldocument_idproject_idx ON finaldocument (idProject);
CREATE INDEX fk_finaldocument_idthesis_idx ON finaldocument (idThesis);

CREATE TABLE evaluationitem (
  idevaluationItem SERIAL NOT NULL,
  idDepartment INT NOT NULL,
  description VARCHAR(255) NOT NULL,
  ponderosity REAL NOT NULL,
  stage SMALLINT NOT NULL,
  active SMALLINT NOT NULL,
  sequence SMALLINT NOT NULL,
  type SMALLINT NOT NULL,
  PRIMARY KEY (idevaluationItem),
  CONSTRAINT fk_evaluationitem_department FOREIGN KEY (idDepartment) REFERENCES department (iddepartment) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_evaluationitem_department_idx ON evaluationitem (idDepartment);

CREATE TABLE jury (
  idjury SERIAL NOT NULL,
  date TIMESTAMP NOT NULL,
  local VARCHAR(100) NOT NULL,
  idproject INT DEFAULT NULL,
  idthesis INT DEFAULT NULL,
  comments text NOT NULL,
  startTime time NOT NULL,
  endTime time NOT NULL,
  minimumScore REAL NOT NULL,
  supervisorabsencereason TEXT NOT NULL,
  supervisorassignsgrades SMALLINT NOT NULL ,
  PRIMARY KEY (idjury),
  CONSTRAINT fk_jury_project FOREIGN KEY (idproject) REFERENCES project (idproject) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_jury_thesis FOREIGN KEY (idthesis) REFERENCES thesis (idthesis) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_jury_project_idx ON jury (idproject);
CREATE INDEX fk_jury_thesis_idx ON jury (idthesis);

CREATE TABLE juryappraiser (
  idjuryappraiser SERIAL NOT NULL,
  idjury INT NOT NULL,
  idappraiser INT NOT NULL,
  file BYTEA NULL,
  additionalfile BYTEA NULL ,
  comments TEXT NOT NULL,
  chair SMALLINT NOT NULL,
  substitute SMALLINT NOT NULL,
  PRIMARY KEY (idjuryappraiser),
  CONSTRAINT fk_juryappraiser_appraiser FOREIGN KEY (idappraiser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_juryappraiser_jury FOREIGN KEY (idjury) REFERENCES jury (idjury) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_juryappraiser_jury_idx ON juryappraiser (idjury);
CREATE INDEX fk_juryappraiser_appraiser_idx ON juryappraiser (idappraiser);

CREATE TABLE juryrequest (
  idjuryrequest SERIAL NOT NULL,
  date TIMESTAMP NOT NULL,
  local VARCHAR(100) NOT NULL,
  idproposal INT NOT NULL,
  stage INT NOT NULL,
  comments text NOT NULL,
  supervisorabsencereason TEXT NOT NULL,
  idjury INT DEFAULT NULL,
  PRIMARY KEY (idjuryrequest),
  CONSTRAINT fk_juryrequest_proposal FOREIGN KEY (idproposal) REFERENCES proposal (idproposal) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_juryrequest_jury FOREIGN KEY (idjury) REFERENCES jury (idjury) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_juryrequest_proposal_idx ON juryrequest (idproposal);
CREATE INDEX fk_juryrequest_jury_idx ON juryrequest (idjury);

CREATE TABLE juryappraiserrequest (
  idjuryappraiserrequest SERIAL NOT NULL,
  idjuryrequest INT NOT NULL,
  idappraiser INT NOT NULL,
  chair SMALLINT NOT NULL,
  substitute SMALLINT NOT NULL,
  PRIMARY KEY (idjuryappraiserrequest),
  CONSTRAINT fk_juryappraiserrequest_appraiser FOREIGN KEY (idappraiser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_juryappraiserrequest_juryrequest FOREIGN KEY (idjuryrequest) REFERENCES juryrequest (idjuryrequest) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_juryappraiserrequest_juryrequest_idx ON juryappraiserrequest (idjuryrequest);
CREATE INDEX fk_juryappraiserrequest_appraiser_idx ON juryappraiserrequest (idappraiser);

CREATE TABLE juryappraiserscore (
  idjuryappraiserscore SERIAL NOT NULL,
  idjuryappraiser INT NOT NULL,
  idevaluationitem INT NOT NULL,
  score REAL NOT NULL,
  PRIMARY KEY (idjuryappraiserscore),
  CONSTRAINT id_juryappraiserscore_evaluationitem FOREIGN KEY (idevaluationitem) REFERENCES evaluationitem (idevaluationItem) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT id_juryappraiserscore_juryappraiser FOREIGN KEY (idjuryappraiser) REFERENCES juryappraiser (idjuryappraiser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX id_juryappraiserscore_juryappraiser_idx ON juryappraiserscore (idjuryappraiser);
CREATE INDEX id_juryappraiserscore_evaluationitem_idx ON juryappraiserscore (idevaluationitem);

CREATE TABLE jurystudent (
  idjurystudent SERIAL NOT NULL,
  idjury INT NOT NULL,
  idstudent INT NOT NULL,
  PRIMARY KEY (idjurystudent),
  CONSTRAINT fk_jurystudent_jury FOREIGN KEY (idjury) REFERENCES jury (idjury) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_jurystudent_student FOREIGN KEY (idstudent) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_jurystudent_jury_idx ON jurystudent (idjury);
CREATE INDEX fk_jurystudent_student_idx ON jurystudent (idstudent);

CREATE  TABLE bugreport (
  idbugreport SERIAL NOT NULL,
  iduser INT NOT NULL,
  module SMALLINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  type SMALLINT NOT NULL,
  reportDate TIMESTAMP NOT NULL,
  status SMALLINT NOT NULL,
  statusDate TIMESTAMP NULL,
  statusDescription TEXT NOT NULL,
  PRIMARY KEY (idbugreport),
  CONSTRAINT fk_bugreport_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_bugreport_user_idx ON bugreport (iduser);

CREATE TABLE message (
    idmessage serial NOT NULL,
    iduser integer NOT NULL,
    title character varying(255) NOT NULL,
    message text NOT NULL,
    read smallint NOT NULL,
    date timestamp NOT NULL,
    module smallint NOT NULL,
    PRIMARY KEY (idmessage),
    CONSTRAINT fk_message_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_message_user_idx ON message (iduser);

CREATE TABLE loginlog (
    idlog bigserial NOT NULL,
    iduser integer NOT NULL,
    event smallint NOT NULL,
    date timestamp NOT NULL,
    source character varying(255) NOT NULL,
    device character varying(255) NOT NULL,
    PRIMARY KEY (idlog),
    CONSTRAINT fk_loginlog_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_loginlog_user_idx ON loginlog (iduser);

CREATE TABLE eventlog (
    idlog bigserial NOT NULL,
    iduser integer NOT NULL,
    event smallint NOT NULL,
    classname character varying(255) NOT NULL,
    idobject integer NOT NULL,
    date timestamp NOT NULL,
    data bytea NOT NULL,
    PRIMARY KEY (ideventlog),
    CONSTRAINT fk_eventlog_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_eventlog_user_idx ON eventlog (iduser);

CREATE TABLE signdocument (
    iddocument serial NOT NULL,
    guid character varying(255) NOT NULL,
    type smallint NOT NULL,
    version smallint NOT NULL,
    idregister integer NOT NULL,
    report bytea NOT NULL,
    dataset bytea NOT NULL,
    generateddate timestamp NOT NULL,
    iddepartment integer NOT NULL,
    PRIMARY KEY (iddocument),
    CONSTRAINT fk_signdocument_department FOREIGN KEY (iddepartment) REFERENCES department (iddepartment) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_signdocument_department_idx ON signdocument (iddepartment);
ALTER TABLE signdocument ADD UNIQUE (guid);

CREATE TABLE signature (
    idsignature serial NOT NULL,
    iddocument integer NOT NULL,
    iduser integer NOT NULL,
    signature bytea,
    signaturedate timestamp,
    revoked smallint NOT NULL,
    revokeddate timestamp,
    idrevokeduser integer,
    PRIMARY KEY (idsignature),
    CONSTRAINT fk_signature_signdocument FOREIGN KEY (iddocument) REFERENCES signdocument (iddocument) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_signature_user FOREIGN KEY (iduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_signature_revokeduser FOREIGN KEY (idrevokeduser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_signature_signdocument_idx ON signature (iddocument);
CREATE INDEX fk_signature_user_idx ON signature (iduser);
CREATE INDEX fk_signature_revokeduser_idx ON signature (idrevokeduser);

CREATE TABLE internshipposterrequest (
    idinternshipposterrequest SERIAL NOT NULL,
    idinternship INT NOT NULL,
    idinternshipjury INT,
    requestdate timestamp NOT NULL,
    PRIMARY KEY (idinternshipposterrequest),
    CONSTRAINT fk_internshipposterrequest_internship FOREIGN KEY (idinternship) REFERENCES internship (idinternship) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_internshipposterrequest_internshipjury FOREIGN KEY (idinternshipjury) REFERENCES internshipjury (idinternshipjury) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_internshipposterrequest_internship_idx ON internshipposterrequest (idinternship);
CREATE INDEX fk_internshipposterrequest_internshipjury_idx ON internshipposterrequest (idinternshipjury);

CREATE TABLE internshipposterappraiserrequest (
    idinternshipposterappraiserrequest SERIAL NOT NULL,
    idinternshipposterrequest INT NOT NULL,
    idappraiser INT NOT NULL,
    substitute smallint NOT NULL,
    PRIMARY KEY (idinternshipposterappraiserrequest),
    CONSTRAINT fk_internshipposterappraiserrequest_internshipposterrequest FOREIGN KEY (idinternshipposterrequest) REFERENCES internshipposterrequest (idinternshipposterrequest) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT fk_internshipposterappraiserrequest_appraiser FOREIGN KEY (idappraiser) REFERENCES "user" (iduser) ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX fk_internshipposterappraiserrequest_internshipposterrequest_idx ON internshipposterappraiserrequest (idinternshipposterrequest);
CREATE INDEX fk_internshipposterappraiserrequest_appraiser_idx ON internshipposterappraiserrequest (idappraiser);

CREATE OR REPLACE FUNCTION year(timestamp) RETURNS integer AS $$
DECLARE
   d ALIAS FOR $1;
BEGIN
   return date_part('year', d);
END;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION month(timestamp) RETURNS integer AS $$
DECLARE
   d ALIAS FOR $1;
BEGIN
   return date_part('month', d);
END;

$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION day(timestamp) RETURNS integer AS $$
DECLARE
   d ALIAS FOR $1;
BEGIN
   return date_part('day', d);
END;

$$ LANGUAGE plpgsql;

INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(1, 2, '', '', '{student};{group};{activity};{semester};{year};{comments}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(2, 2, '', '', '{student};{group};{activity};{semester};{year};{comments};{feedbackUser};{feedback}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(3, 3, '', '', '{student};{company};{companySupervisor};{supervisor};{type};{startDate};{comments}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(4, 3, '', '', '{student};{company};{companySupervisor};{supervisor};{type};{startDate};{comments}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(5, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(6, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(7, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(8, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(9, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(10, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(11, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea};{appraiser};{manager};{availabledate}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(12, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea};{appraiser};{feedback};{comments};{manager}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(13, 1, '', '', '{student};{title};{appraiser};{date};{time};{local};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(14, 1, '', '', '{student};{title};{appraiser};{date};{time};{local};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(15, 1, '', '', '{student};{title};{appraiser};{date};{time};{local};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(16, 1, '', '', '{student};{title};{appraiser};{date};{time};{local};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(17, 1, '', '', '{student};{title};{appraiser};{date};{time};{local};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(18, 3, '', '', '{student};{title};{appraiser};{company};{date};{time};{local}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(19, 3, '', '', '{student};{title};{appraiser};{company};{date};{time};{local}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(20, 3, '', '', '{student};{title};{appraiser};{company};{date};{time};{local}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(21, 3, '', '', '{student};{title};{appraiser};{company};{date};{time};{local}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(22, 3, '', '', '{student};{title};{appraiser};{company};{date};{time};{local}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(23, 1, '', '', '{documenttype};{student};{title};{supervisor}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(24, 1, '', '', '{documenttype};{student};{title};{supervisor};{feedback};{manager}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(25, 3, '', '', '{student};{company};{supervisor}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(26, 3, '', '', '{student};{company};{supervisor};{feedback};{manager}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(27, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea};{appraiser};{manager};{availabledate}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(28, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea};{appraiser};{feedback};{comments}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(29, 1, '', '', '{student};{supervisor};{cosupervisor};{title};{subarea};{appraiser};{feedback};{comments}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(30, 0, '', '', '{name};{profile};{email};{host}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(31, 1, '', '', '{student};{supervisor};{documenttype};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(32, 1, '', '', '{student};{supervisor};{documenttype};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(33, 1, '', '', '{student};{supervisor};{documenttype};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(34, 1, '', '', '{student};{supervisor};{documenttype};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(35, 1, '', '', '{documenttype};{student};{title};{supervisor}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(36, 1, '', '', '{documenttype};{student};{title};{supervisor};{feedback}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(37, 3, '', '', '{student};{company};{supervisor}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(38, 3, '', '', '{student};{company};{supervisor};{feedback}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(39, 0, '', '', '{type};{title};{description}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(40, 0, '', '', '{type};{title};{description};{status};{statusdescription};{user}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(41, 3, '', '', '{student};{supervisor};{company};{type}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(42, 3, '', '', '{student};{supervisor};{manager};{company};{type}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(43, 1, '', '', '{manager};{student};{supervisor};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(44, 1, '', '', '{manager};{appraiser};{student};{supervisor};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(45, 1, '', '', '{manager};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(46, 1, '', '', '{manager};{type};{name};{title}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(47, 1, '', '', '{manager};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(48, 1, '', '', '{manager};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(49, 3, '', '', '{manager};{student};{supervisor};{company}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(50, 3, '', '', '{manager};{student};{supervisor};{company}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(51, 1, '', '', '{name};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(52, 1, '', '', '{name};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(53, 1, '', '', '{name};{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(54, 3, '', '', '{name};{student};{supervisor};{company}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(55, 3, '', '', '{name};{student};{supervisor};{company}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(56, 1, '', '', '{student};{supervisor};{title};{stage}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(57, 3, '', '', '{student};{supervisor};{company}');
INSERT INTO emailmessage(idemailmessage, module, subject, message, datafields) VALUES(58, 3, '', '', '{student};{supervisor};{manager};{company};{type}');
