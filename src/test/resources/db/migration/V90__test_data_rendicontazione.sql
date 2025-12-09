-- Test Data for Rendicontazione Flow Integration Tests
-- Flyway Migration: V90__test_data_rendicontazione.sql
-- This file is automatically executed by Flyway during test database initialization
-- Location: src/test/resources/db/migration/V90__test_data_rendicontazione.sql


-- ===================================================================
-- 1. Insert CBILL_ABI_FEDERAZIONE records
-- ===================================================================

-- SENDER_BANK is always equals to ABI in PAGOPA_TRANSACTIONS table

-- ABI 09514 -> BNLIITRR (PSP Fiscal Code: 05963231005)
INSERT INTO atm_layer_reporting_schema.cbill_abi_federazione (abi, pagopa_id, psp_fiscal_code, psp_channel, pagopa_direct)
VALUES ('09514', 'BNLIITRR', '05963231005', '_03', true)
ON CONFLICT (abi) DO NOTHING;

-- PSP AGID_01
INSERT INTO atm_layer_reporting_schema.cbill_abi_federazione (abi, pagopa_id, psp_fiscal_code, psp_channel, pagopa_direct)
VALUES ('12345', 'AGID_01', '97735020584', '_03', false)
ON CONFLICT (abi) DO NOTHING;

-- PSP AGID_02
INSERT INTO atm_layer_reporting_schema.cbill_abi_federazione (abi, pagopa_id, psp_fiscal_code, psp_channel, pagopa_direct)
VALUES ('12346', 'AGID_02', '97735020584', '_05', false)
ON CONFLICT (abi) DO NOTHING;

-- ===================================================================
-- 2. Insert PAGOPA_TRANSACTIONS records
-- ===================================================================

-- Transaction 1: AGID_01 (idPSP: AGID_01)
INSERT INTO atm_layer_reporting_schema.pagopa_transactions (
    id, transaction_id, status, bill_account_id, bill_amount, sender_bank, pay_date,
    reported, biller_iban, bill_id, biller_commission, bank_commission,
    idempotency_key, biller_fiscal_code, notice_number, ret_code, outcome_code,
    pay_description, biller_name, biller_office, pay_token, token_exp_dt,
    crd_reference_id, atm_code
) VALUES (
    1,
    '550e8400-e29b-41d4-a716-446655440001',
    'A',
    'ACC001',
    100.50,
    '12345',
    NOW(),
    false,
    'IT60X0542811101000000123456',
    '300002700000001',
    1.00,
    2.00,
    'IDEMPOTENCY001',
    '97735020584',
    '012210209926737900',
    '0000',
    'O',
    'Pagamento bollettino',
    'Test Organization 1',
    'Test Office 1',
    '2130101502302932577',
    NOW() + INTERVAL '1 day',
    'CRD001',
    'ATM001'
) ON CONFLICT (transaction_id) DO NOTHING;

-- Transaction 2: AGID_02 (idPSP: AGID_02)
INSERT INTO atm_layer_reporting_schema.pagopa_transactions (
    id, transaction_id, status, bill_account_id, bill_amount, sender_bank, pay_date,
    reported, biller_iban, bill_id, biller_commission, bank_commission,
    idempotency_key, biller_fiscal_code, notice_number, ret_code, outcome_code,
    pay_description, biller_name, biller_office, pay_token, token_exp_dt,
    crd_reference_id, atm_code
) VALUES (
    2,
    '550e8400-e29b-41d4-a716-446655440002',
    'A',
    'ACC002',
    250.75,
    '12346',
    NOW(),
    false,
    'IT60X0542811101000000123457',
    '300002700000002',
    2.50,
    3.00,
    'IDEMPOTENCY002',
    '97735020584',
    '012210209926737901',
    '0000',
    'O',
    'Pagamento bollettino',
    'Test Organization 2',
    'Test Office 2',
    '2130101502302932578',
    NOW() + INTERVAL '1 day',
    'CRD002',
    'ATM002'
) ON CONFLICT (transaction_id) DO NOTHING;

-- Transaction 2b: Additional AGID_02 (for testing aggregation)
INSERT INTO atm_layer_reporting_schema.pagopa_transactions (
    id, transaction_id, status, bill_account_id, bill_amount, sender_bank, pay_date,
    reported, biller_iban, bill_id, biller_commission, bank_commission,
    idempotency_key, biller_fiscal_code, notice_number, ret_code, outcome_code,
    pay_description, biller_name, biller_office, pay_token, token_exp_dt,
    crd_reference_id, atm_code
) VALUES (
    4,
    '550e8400-e29b-41d4-a716-4466554400b2',
    'A',
    'ACC002b',
    150.00,
    '12346',
    NOW(),
    false,
    'IT60X0542811101000000999999',
    '300002700000002b',
    1.50,
    2.00,
    'IDEMPOTENCY002b',
    '97735020584',
    '012210209926737901b',
    '0000',
    'O',
    'Pagamento bollettino',
    'Test Organization 2b',
    'Test Office 2b',
    '2130101502302932578b',
    NOW() + INTERVAL '1 day',
    'CRD002b',
    'ATM002b'
) ON CONFLICT (transaction_id) DO NOTHING;

-- Transaction 3: BNLIITRR (Bank transfer with ABI 09514)
INSERT INTO atm_layer_reporting_schema.pagopa_transactions (
    id, transaction_id, status, bill_account_id, bill_amount, sender_bank, pay_date,
    reported, biller_iban, bill_id, biller_commission, bank_commission,
    idempotency_key, biller_fiscal_code, notice_number, ret_code, outcome_code,
    pay_description, biller_name, biller_office, pay_token, token_exp_dt,
    crd_reference_id, atm_code
) VALUES (
    3,
    '550e8400-e29b-41d4-a716-446655440003',
    'A',
    'ACC003',
    500.00,
    '09514',
    NOW(),
    false,
    'IT60X0542811101000000123458',
    '300002700000003',
    5.00,
    4.50,
    'IDEMPOTENCY003',
    '05963231005',
    '012210209926737902',
    '0000',
    'O',
    'Pagamento bollettino BNL',
    'Bank Lazio Name',
    'Bank Lazio Office',
    '2130101502302932579',
    NOW() + INTERVAL '1 day',
    'CRD003',
    'ATM003'
) ON CONFLICT (transaction_id) DO NOTHING;

-- ===================================================================
-- 3. Insert PAGOPA_TRANSFER_LIST records
-- ===================================================================

-- Transfer 1: for Transaction 1 (AGID_01)
-- PA Fiscal Code: 97735020584 (derived from biller_fiscal_code)
INSERT INTO atm_layer_reporting_schema.pagopa_transfer_list (
    id, transaction_id, transfer_id, transfer_amount, transfer_cro, flow_id,
    pagopa_reported, transfer_execution_dt, pa_fiscal_code, pa_name, pa_iban, rmt_info
) VALUES (
    1,
    1,
    1,
    100.50,
    'CRO00000001',
    'FLW00000001',
    false,
    CURRENT_DATE,
    '97735020584',
    'PA Name 1',
    'IT60X0542811101000000123456',
    'RMT Info 1'
) ON CONFLICT (id) DO NOTHING;

-- Transfer 2: for Transaction 2 (AGID_02)
INSERT INTO atm_layer_reporting_schema.pagopa_transfer_list (
    id, transaction_id, transfer_id, transfer_amount, transfer_cro, flow_id,
    pagopa_reported, transfer_execution_dt, pa_fiscal_code, pa_name, pa_iban, rmt_info
) VALUES (
    2,
    2,
    1,
    250.75,
    'CRO00000002',
    'FLW00000002',
    false,
    CURRENT_DATE,
    '97735020584',
    'PA Name 2',
    'IT60X0542811101000000123457',
    'RMT Info 2'
) ON CONFLICT (id) DO NOTHING;

-- Transfer 2b: for Transaction 2b (additional AGID_02 for aggregation testing)
INSERT INTO atm_layer_reporting_schema.pagopa_transfer_list (
    id, transaction_id, transfer_id, transfer_amount, transfer_cro, flow_id,
    pagopa_reported, transfer_execution_dt, pa_fiscal_code, pa_name, pa_iban, rmt_info
) VALUES (
    4,
    2,
    1,
    150.00,
    'CRO00000002b',
    'FLW00000002b',
    false,
    CURRENT_DATE,
    '97735020584',
    'PA Name 2b',
    'IT60X0542811101000000999999',
    'RMT Info 2b'
) ON CONFLICT (id) DO NOTHING;

-- Transfer 3: for Transaction 3 (BNLIITRR)
INSERT INTO atm_layer_reporting_schema.pagopa_transfer_list (
    id, transaction_id, transfer_id, transfer_amount, transfer_cro, flow_id,
    pagopa_reported, transfer_execution_dt, pa_fiscal_code, pa_name, pa_iban, rmt_info
) VALUES (
    3,
    3,
    1,
    500.00,
    'CRO00000003',
    'FLW00000003',
    false,
    CURRENT_DATE,
    '05963231005',
    'PA Name 3',
    'IT60X0542811101000000123458',
    'RMT Info 3'
) ON CONFLICT (id) DO NOTHING;

