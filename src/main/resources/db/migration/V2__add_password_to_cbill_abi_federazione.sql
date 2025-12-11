ALTER TABLE atm_layer_reporting_schema.cbill_abi_federazione
    ADD COLUMN IF NOT EXISTS password VARCHAR(255) DEFAULT '' NOT NULL;
