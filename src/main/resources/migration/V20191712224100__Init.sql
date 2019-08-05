CREATE TABLE IF NOT EXISTS admin_credentials(
  chat_id VARCHAR(512) NOT NULL,
  full_name VARCHAR(512),
  created_at TIMESTAMP,
  deleted_at TIMESTAMP,
  PRIMARY KEY(chat_id)
);

CREATE TABLE IF NOT EXISTS countries(
  country_name VARCHAR(512),
  PRIMARY KEY(country_name)
);

CREATE TABLE IF NOT EXISTS registered_appointment(
  id SERIAL,
  chat_id varchar(256),
  full_name VARCHAR(256),
  flight_date VARCHAR(512),
  file_id VARCHAR(1024),
  created_at TIMESTAMP,
  deleted_at TIMESTAMP,
  PRIMARY KEY (id)
);


CREATE INDEX admin_credentials_idx ON admin_credentials(chat_id);

CREATE INDEX registered_appointment_idx ON registered_appointment(chat_id);
