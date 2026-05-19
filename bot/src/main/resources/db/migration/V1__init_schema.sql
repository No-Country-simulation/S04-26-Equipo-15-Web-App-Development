CREATE TABLE pipeline_run (
    id               BIGSERIAL PRIMARY KEY,
    status           VARCHAR(20)  NOT NULL,
    started_at       TIMESTAMP    NOT NULL,
    finished_at      TIMESTAMP,
    drafts_generated INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE weekly_activity (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(500)  NOT NULL,
    url          VARCHAR(1000),
    author       VARCHAR(100),
    upvotes      INTEGER       NOT NULL DEFAULT 0,
    comments     INTEGER       NOT NULL DEFAULT 0,
    score        INTEGER       NOT NULL DEFAULT 0,
    type         VARCHAR(20)   NOT NULL,
    week_of      DATE          NOT NULL,
    collected_at TIMESTAMP     NOT NULL
);

CREATE TABLE draft (
    id              BIGSERIAL PRIMARY KEY,
    channel         VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    content         TEXT         NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    pipeline_run_id BIGINT       REFERENCES pipeline_run(id)
);

CREATE TABLE draft_history (
    id               BIGSERIAL PRIMARY KEY,
    draft_id         BIGINT       NOT NULL REFERENCES draft(id),
    previous_content TEXT         NOT NULL,
    saved_at         TIMESTAMP    NOT NULL
);
