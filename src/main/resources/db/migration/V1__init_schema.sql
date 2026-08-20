-- School Bulletin: academic domain schema.
-- Table and column names here MUST match the @Table/@Column mappings in
-- src/main/java/api/poja/app/jpa/*.java exactly, since spring.jpa.hibernate.ddl-auto
-- is set to "validate": Flyway owns the schema, Hibernate only checks it matches.

CREATE TABLE role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE app_user
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    full_name  VARCHAR(200) NOT NULL,
    role_id    BIGINT       NOT NULL REFERENCES role (id),
    student_id BIGINT,
    teacher_id BIGINT
);

CREATE TABLE promotion
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    start_year INTEGER      NOT NULL,
    end_year   INTEGER      NOT NULL
);

CREATE TABLE teacher
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    user_id    BIGINT
);

CREATE TABLE student
(
    id           BIGSERIAL PRIMARY KEY,
    std          VARCHAR(50)  NOT NULL UNIQUE,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    promotion_id BIGINT       NOT NULL REFERENCES promotion (id),
    user_id      BIGINT
);

CREATE TABLE student_group
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    promotion_id BIGINT       NOT NULL REFERENCES promotion (id)
);

CREATE TABLE student_group_assignment
(
    id         BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES student (id),
    group_id   BIGINT NOT NULL REFERENCES student_group (id),
    start_date DATE   NOT NULL,
    end_date   DATE
);

CREATE TABLE course
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(30)  NOT NULL UNIQUE
);

CREATE TABLE course_teaching
(
    id            BIGSERIAL PRIMARY KEY,
    course_id     BIGINT     NOT NULL REFERENCES course (id),
    teacher_id    BIGINT     NOT NULL REFERENCES teacher (id),
    group_id      BIGINT     NOT NULL REFERENCES student_group (id),
    academic_year VARCHAR(9) NOT NULL,
    CONSTRAINT uq_course_teaching UNIQUE (course_id, teacher_id, group_id, academic_year)
);

CREATE TABLE grade
(
    id             BIGSERIAL PRIMARY KEY,
    student_id     BIGINT           NOT NULL REFERENCES student (id),
    course_id      BIGINT           NOT NULL REFERENCES course (id),
    academic_year  VARCHAR(9)       NOT NULL,
    value          DOUBLE PRECISION NOT NULL,
    author_user_id BIGINT           NOT NULL REFERENCES app_user (id),
    created_at     TIMESTAMP        NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE TABLE grade_history
(
    id             BIGSERIAL PRIMARY KEY,
    grade_id       BIGINT           NOT NULL REFERENCES grade (id),
    old_value      DOUBLE PRECISION,
    new_value      DOUBLE PRECISION NOT NULL,
    reason         VARCHAR(500)     NOT NULL,
    author_user_id BIGINT           NOT NULL REFERENCES app_user (id),
    changed_at     TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE INDEX idx_grade_student ON grade (student_id);
CREATE INDEX idx_grade_course ON grade (course_id);
CREATE INDEX idx_sga_student ON student_group_assignment (student_id);
CREATE INDEX idx_sga_group ON student_group_assignment (group_id);
CREATE INDEX idx_ct_teacher ON course_teaching (teacher_id);
CREATE INDEX idx_ct_group ON course_teaching (group_id);
CREATE INDEX idx_grade_history_grade ON grade_history (grade_id);

INSERT INTO role (name)
VALUES ('STUDENT'),
       ('TEACHER'),
       ('ADMIN');
