INSERT INTO users (student_id, name, role)
VALUES ('admin', '관리자', 'ADMIN')
ON CONFLICT (student_id) DO NOTHING;