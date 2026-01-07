CREATE SCHEMA IF NOT EXISTS atm_layer_reporting_schema;

CREATE TABLE IF NOT EXISTS atm_layer_reporting_schema.pagopa_transactions (
    id BIGINT PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(1) NOT NULL,
    bill_account_id VARCHAR(36) NOT NULL,
    bill_amount NUMERIC NOT NULL,
    sender_bank VARCHAR(5) NOT NULL,
    pay_date TIMESTAMPTZ NOT NULL,
    reported BOOLEAN NOT NULL,
    biller_iban VARCHAR(27) NOT NULL,
    bill_id VARCHAR(18) NOT NULL,
    biller_commission NUMERIC NOT NULL,
    bank_commission NUMERIC NOT NULL,
    idempotency_key VARCHAR(22) NOT NULL,
    biller_fiscal_code VARCHAR(11) NOT NULL,
    notice_number VARCHAR(36) NOT NULL,
    ret_code VARCHAR(4) NOT NULL,
    outcome_code VARCHAR(1) NOT NULL,
    pay_description VARCHAR(210) NOT NULL,
    biller_name VARCHAR(140) NOT NULL,
    biller_office VARCHAR(140) NOT NULL,
    pay_opt_amount NUMERIC,
    pay_opt_type VARCHAR(3),
    pay_opt_duedate TIMESTAMPTZ,
    pay_opt_note VARCHAR(210),
    pay_token VARCHAR(35) NOT NULL,
    token_exp_dt TIMESTAMPTZ NOT NULL,
    crd_reference_id VARCHAR(35) NOT NULL,
    atm_code VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS atm_layer_reporting_schema.pagopa_transfer_list (
    id BIGINT PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    transfer_id INTEGER NOT NULL,
    transfer_amount NUMERIC NOT NULL,
    transfer_cro VARCHAR(35),
    flow_id VARCHAR(35),
    pagopa_reported BOOLEAN NOT NULL,
    transfer_execution_dt DATE,
    pa_fiscal_code VARCHAR(11) NOT NULL,
    pa_name VARCHAR(140),
    pa_iban VARCHAR(34) NOT NULL,
    rmt_info VARCHAR(140) NOT NULL,
    CONSTRAINT fk_pagopa_transfer_list_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES pagopa_transactions(id)
);

CREATE TABLE IF NOT EXISTS atm_layer_reporting_schema.cbill_abi_federazione (
    abi VARCHAR(5) PRIMARY KEY,
    pagopa_id VARCHAR(35) NOT NULL,
    psp_fiscal_code VARCHAR(11) NOT NULL,
    psp_channel VARCHAR(5) NOT NULL,
    pagopa_direct BOOLEAN
);

CREATE INDEX IF NOT EXISTS idx_pagopa_transfer_list_transaction
    ON pagopa_transfer_list (transaction_id);
