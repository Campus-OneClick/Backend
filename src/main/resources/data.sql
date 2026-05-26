INSERT INTO users (student_id, name, email, role, department, firebase_uid)
VALUES ('admin', '관리자', 'admin@campus-oneclick.com', 'ADMIN', '컴퓨터공학과', 'IG1lZaVmtCcqR41vVxJ4OvoXNmH2')
ON CONFLICT (student_id) DO NOTHING;