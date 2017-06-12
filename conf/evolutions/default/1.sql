# -- Schema

# --- !Ups

CREATE TABLE users (
  id          SERIAL PRIMARY KEY,
  username    VARCHAR(255) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  token       TEXT DEFAULT NULL UNIQUE,
  created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
  modified    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX user_usernames ON users (username);
CREATE INDEX user_passwords ON users (password);
CREATE UNIQUE INDEX user_tokens ON users (token);

# --- !Downs

DROP TABLE users;
