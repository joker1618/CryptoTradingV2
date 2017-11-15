drop DATABASE CRYPTO_TRADING;
CREATE DATABASE IF NOT EXISTS CRYPTO_TRADING;

USE CRYPTO_TRADING;

-- TABLES STRUCTURES
CREATE TABLE IF NOT EXISTS ASSETS (
	ASSET_NAME			VARCHAR(10) NOT NULL,
	A_CLASS 			VARCHAR(20),
	ALT_NAME			VARCHAR(10),
	DECIMALS			INT,
	DISPLAY_DECIMALS	INT,
	START_TIME			BIGINT NOT NULL,
	EXPIRE_TIME			BIGINT NOT NULL,
	PRIMARY KEY(ASSET_NAME, START_TIME)
);


CREATE TABLE IF NOT EXISTS ASSET_PAIRS (
	PAIR_ID				BIGINT PRIMARY KEY,
	PAIR_NAME			VARCHAR(20) NOT NULL,
	ALT_NAME			VARCHAR(20),
	A_CLASS_BASE		VARCHAR(20),
	BASE				VARCHAR(10),
	A_CLASS_QUOTE		VARCHAR(20),
	QUOTE				VARCHAR(10),
	LOT					VARCHAR(20),
	PAIR_DECIMALS		INT,
	LOT_DECIMALS		INT,
	LOT_MULTIPLIER		INT,
	FEE_VOLUME_CURRENCY	VARCHAR(10),
	MARGIN_CALL			INT,
	MARGIN_STOP			INT,
	START_TIME			BIGINT NOT NULL,
	EXPIRE_TIME			BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS ASSET_PAIRS_FEE (
	PAIR_ID			BIGINT,
	FEE_TYPE		ENUM('fees', 'fees_maker'),
	VOLUME			INT,
	PERCENT_FEE		DECIMAL(10,5)
);
CREATE TABLE IF NOT EXISTS ASSET_PAIRS_LEVERAGE (
	PAIR_ID			BIGINT,
	LEVERAGE_TYPE	ENUM('buy', 'sell'),
	LEVERAGE_VALUE	INT
);


CREATE TABLE IF NOT EXISTS TICKERS (
    CALL_TIME		        BIGINT      NOT NULL,
	PAIR_NAME               VARCHAR(20) NOT NULL,
    ASK_PRICE		        DECIMAL(30,11),
	ASK_WHOLE_LOT_VOLUME    INT,
	ASK_LOT_VOLUME          DECIMAL(21,11),
    BID_PRICE		        DECIMAL(21,11),
	BID_WHOLE_LOT_VOLUME    INT,
	BID_LOT_VOLUME          DECIMAL(21,11),
    LAST_CLOSED_PRICE		DECIMAL(21,11),
	LAST_CLOSED_LOT_VOLUME  DECIMAL(21,11),
    VOLUME_TODAY		    DECIMAL(21,11),
	VOLUME_LAST_24          DECIMAL(21,11),
    VOLUME_WEIGHTED_AVERAGE_TODAY		DECIMAL(21,11),
	VOLUME_WEIGHTED_AVERAGE_LAST_24     DECIMAL(21,11),
    NUMBER_TRADES_TODAY		DECIMAL(21,11),
	NUMBER_TRADES_LAST_24   DECIMAL(21,11),
    LOW_TODAY		        DECIMAL(21,11),
	LOW_LAST_24             DECIMAL(21,11),
    HIGH_TODAY		        DECIMAL(21,11),
	HIGH_LAST_24            DECIMAL(21,11),
	OPENING_PRICE           DECIMAL(21,11),
	GRAFANA_TIME            DATETIME NOT NULL,

	PRIMARY KEY (CALL_TIME, PAIR_NAME)
);
CREATE INDEX _TICKERS_IDX_1 ON TICKERS (CALL_TIME);
CREATE INDEX _TICKERS_IDX_2 ON TICKERS (PAIR_NAME);


CREATE TABLE IF NOT EXISTS CT_USERS (
    USER_ID         INT PRIMARY KEY,
    USER_NAME       VARCHAR(50) UNIQUE NOT NULL,
    API_KEY         VARCHAR(100) NOT NULL,
    API_SECRET      VARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS CT_PROPERTIES (
    USER_ID         INT NOT NULL,
    PROP_NAME       VARCHAR(50) NOT NULL,
    PROP_VALUE      VARCHAR(100),
    PRIMARY KEY (USER_ID, PROP_NAME)
);


CREATE TABLE IF NOT EXISTS ORDERS (
    ORDER_TX_ID		VARCHAR(30) NOT NULL,
    USER_ID         INT NOT NULL,
    REF_ID          VARCHAR(30),
    USER_REF        VARCHAR(30),
    STATUS          ENUM('pending', 'open', 'closed', 'canceled', 'expired'),
    REASON          VARCHAR(100),   -- only for closed orders
    OPENTM          BIGINT,
    CLOSETM         BIGINT, 		-- only for closed orders
    STARTTM         BIGINT,
    EXPIRETM        BIGINT,
    VOL             DECIMAL(30,11),
    VOL_EXEC        DECIMAL(21,11),
    COST            DECIMAL(21,11),
    FEE             DECIMAL(21,11),
    AVG_PRICE       DECIMAL(21,11),
    STOP_PRICE      DECIMAL(21,11),
    LIMIT_PRICE     DECIMAL(21,11),
    MISC            VARCHAR(50),
    OFLAGS          VARCHAR(50),
    TRADES_ID       VARCHAR(200),
    DESCR_PAIR_NAME     VARCHAR(20),
    DESCR_ORDER_ACTION  ENUM('buy', 'sell'),
    DESCR_ORDER_TYPE    ENUM('market', 'limit', 'stop-loss', 'take-profit', 'stop-loss-profit', 'stop-loss-profit-limit', 'stop-loss-limit', 'take-profit-limit', 'trailing-stop', 'trailing-stop-limit', 'stop-loss-and-limit', 'settle-position'),
    DESCR_PRICE         DECIMAL(21,11),
    DESCR_PRICE2        DECIMAL(21,11),
    DESCR_LEVERAGE      INT,
    DESCR_ORDER_DESCR   VARCHAR(100),
    DESCR_CLOSE_DESCR   VARCHAR(100),
    PRIMARY KEY(ORDER_TX_ID, USER_ID)
);
CREATE INDEX _ORDERS_IDX_1 ON ORDERS(ORDER_TX_ID, USER_ID);
CREATE INDEX _ORDERS_IDX_2 ON ORDERS(STATUS);


CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE (
        USER_ID         BIGINT NOT NULL,
        CALL_TIME       BIGINT NOT NULL,
        ASSET_NAME      VARCHAR(10) NOT NULL,
        BALANCE         DECIMAL(21,11) NOT NULL,
        VALID           INT     NOT NULL,
        PRIMARY KEY(USER_ID, CALL_TIME, ASSET_NAME)
);
CREATE INDEX _AB_INDEX_1 ON ACCOUNT_BALANCE(USER_ID, VALID);



