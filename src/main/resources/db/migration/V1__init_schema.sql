CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE associate (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    cpf VARCHAR(11) UNIQUE,
    name VARCHAR(255)
);

CREATE TABLE topic (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE voting_session (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    topic_id UUID NOT NULL,
    opening_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closing_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_session_topic FOREIGN KEY (topic_id) REFERENCES topic (id)
);

CREATE TABLE vote (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    voting_session_id UUID NOT NULL,
    associate_id UUID NOT NULL,
    vote_choice VARCHAR(3) NOT NULL, 
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vote_session FOREIGN KEY (voting_session_id) REFERENCES voting_session (id),
    CONSTRAINT fk_vote_associate FOREIGN KEY (associate_id) REFERENCES associate (id),
    CONSTRAINT uk_vote_session_associate UNIQUE (voting_session_id, associate_id)
);

CREATE INDEX idx_voting_session_status ON voting_session (status);
CREATE INDEX idx_vote_session_id ON vote (voting_session_id);