# -- Addresses Schema

# --- !Ups

CREATE TABLE addresses (
  id          SERIAL PRIMARY KEY,
  user_id     INTEGER NOT NULL REFERENCES users(id),
  value       VARCHAR(255) NOT NULL
);

CREATE INDEX address_users ON addresses (user_id);

# --- !Downs

DROP TABLE addresses;
