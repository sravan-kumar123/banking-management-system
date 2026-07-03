--------------------------------------------------------------------------
-- Banking Management System - Oracle Database Schema
-- Run this script as the target schema user before starting the app.
--------------------------------------------------------------------------

-- Drop existing objects (ignore errors if they don't exist yet)
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE transactions';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE accounts';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE accounts_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE transactions_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

--------------------------------------------------------------------------
-- ACCOUNTS
--------------------------------------------------------------------------
CREATE SEQUENCE accounts_seq START WITH 1001 INCREMENT BY 1 NOCACHE;

CREATE TABLE accounts (
    account_id           NUMBER          PRIMARY KEY,
    account_holder_name  VARCHAR2(100)   NOT NULL,
    account_type         VARCHAR2(20)    DEFAULT 'SAVINGS' NOT NULL
                             CHECK (account_type IN ('SAVINGS', 'CURRENT')),
    balance              NUMBER(15,2)    DEFAULT 0 NOT NULL
                             CHECK (balance >= 0),
    email                VARCHAR2(100),
    phone_number         VARCHAR2(20),
    created_date         TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL
);

--------------------------------------------------------------------------
-- TRANSACTIONS
--------------------------------------------------------------------------
CREATE SEQUENCE transactions_seq START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE transactions (
    transaction_id    NUMBER          PRIMARY KEY,
    account_id        NUMBER          NOT NULL,
    transaction_type  VARCHAR2(20)    NOT NULL
                          CHECK (transaction_type IN
                              ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT')),
    amount            NUMBER(15,2)    NOT NULL CHECK (amount > 0),
    balance_after     NUMBER(15,2)    NOT NULL,
    remarks           VARCHAR2(200),
    transaction_date  TIMESTAMP       DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts (account_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_transactions_account_id ON transactions (account_id);

--------------------------------------------------------------------------
-- Sample data (optional - comment out if not needed)
--------------------------------------------------------------------------
-- INSERT INTO accounts (account_id, account_holder_name, account_type, balance, email, phone_number)
-- VALUES (accounts_seq.NEXTVAL, 'John Doe', 'SAVINGS', 5000, 'john.doe@example.com', '9876543210');
--
-- COMMIT;
