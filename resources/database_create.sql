
# PROJECT CREATION AND FASTA UPLOAD

drop table protein_sequence;
CREATE TABLE protein_sequence (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
	sequence MEDIUMTEXT NOT NULL
);
alter table protein_sequence add index(sequence(1000));


DROP TABLE fasta_file;
CREATE TABLE fasta_file (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(200) NOT NULL,
    sha1sum VARCHAR(100) NOT NULL,
    is_processed CHAR(1) DEFAULT 'F' NOT NULL
);
ALTER TABLE fasta_file ADD UNIQUE INDEX( filename, sha1sum);

DROP TABLE fasta_upload;
CREATE TABLE fasta_upload (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
	unique_id CHAR(16) NOT NULL,
	fasta_file_id INT UNSIGNED DEFAULT NULL,
	nickname VARCHAR(2000),
	blast_database_id INT UNSIGNED NOT NULL,
	blast_cutoff VARCHAR(255) NOT NULL,
	blast_only_top_hit CHAR(1) NOT NULL,
	email_address VARCHAR(2000),
	upload_date TIMESTAMP
);
ALTER TABLE fasta_upload ADD UNIQUE INDEX(unique_id);

DROP TABLE fasta_file_protein_sequence;
CREATE TABLE fasta_file_protein_sequence (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    protein_sequence_id INT UNSIGNED NOT NULL,
	fasta_file_id INT UNSIGNED NOT NULL,
	name VARCHAR(2000) NOT NULL,
	description VARCHAR(2000)
);
ALTER TABLE fasta_file_protein_sequence ADD INDEX(fasta_file_id, protein_sequence_id);





# UPLOAD OF MS/MS RUN RESULTS



# POPULATE IMMEDIATELY UPON UPLOAD OF MS RESULTS
DROP TABLE run;
CREATE TABLE run (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
	fasta_upload_id INT UNSIGNED NOT NULL,
	nickname VARCHAR(2000),
	file_name VARCHAR(2000),
	is_processed CHAR(1) DEFAULT 'F' NOT NULL,
	upload_date TIMESTAMP
);




# POPULATE VIA JOBCENTER JOB
DROP TABLE peptide;
CREATE TABLE peptide (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    peptide_sequence VARCHAR(2000) NOT NULL
);
ALTER TABLE peptide ADD INDEX(peptide_sequence(1000));

DROP TABLE peptide_protein_fasta_map;
CREATE TABLE peptide_protein_fasta_map (
	fasta_file_id INT UNSIGNED NOT NULL,
	protein_sequence_id INT UNSIGNED NOT NULL,
	peptide_id INT UNSIGNED NOT NULL
);
ALTER TABLE peptide_protein_fasta_map ADD PRIMARY KEY( fasta_file_id, protein_sequence_id, peptide_id );

DROP TABLE run_peptide_counts;
CREATE TABLE run_peptide_counts (
	run_id INT UNSIGNED NOT NULL,
    peptide_id INT UNSIGNED NOT NULL,
	int count INT UNSIGNED NOT NULL
);
ALTER TABLE run_peptide_counts ADD PRIMARY KEY(run_id, peptide_sequence);

DROP TABLE run_go_counts;
CREATE TABLE run_go_counts (
    run_id INT UNSIGNED NOT NULL,
    go_acc CHAR(10) NOT NULL,
    go_count INT UNSIGNED NOT NULL,
    ratio DOUBLE UNSIGNED NOT NULL
);
ALTER TABLE run_go_counts ADD PRIMARY KEY (run_id, go_acc);


# STORAGE OF GO/PEPTIDE ASSOCIATIONS
DROP TABLE go_peptide_map;
CREATE TABLE go_peptide_map (
    fasta_upload_id INT UNSIGNED NOT NULL,
    peptide_id INT UNSIGNED NOT NULL,
    go_acc CHAR(10) NOT NULL
);
ALTER TABLE go_peptide_map ADD PRIMARY KEY(fasta_upload_id, peptide_id, go_acc);


# UPLOAD / STORAGE OF BLAST HITS

DROP TABLE blast_result;
CREATE TABLE blast_result (
   id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
   fasta_upload_id INT UNSIGNED NOT NULL,
   protein_sequence_id INT UNSIGNED NOT NULL,
   uniprot_acc VARCHAR(255) NOT NULL,
   blast_score DOUBLE NOT NULL
);
ALTER TABLE blast_result ADD UNIQUE INDEX(fasta_upload_id, protein_sequence_id, uniprot_acc);

