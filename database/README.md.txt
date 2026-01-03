## Database Setup

This project uses two MySQL databases:
- university_auth_db
- university_erp_db

### Steps to setup

1. Create databases
CREATE DATABASE university_auth_db;
CREATE DATABASE university_erp_db;

2. Import schema
mysql -u root -p university_auth_db < auth_schema.sql
mysql -u root -p university_erp_db  < erp_schema.sql
