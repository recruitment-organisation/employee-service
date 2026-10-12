ALTER TABLE employee_roles ADD COLUMN IF NOT EXISTS company_id BIGINT;

DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    FOR constraint_name IN
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'employee_roles'::regclass
          AND contype = 'u'
          AND pg_get_constraintdef(oid) = 'UNIQUE (name)'
    LOOP
        EXECUTE format('ALTER TABLE employee_roles DROP CONSTRAINT %I', constraint_name);
    END LOOP;
END $$;

DO $$ BEGIN
    ALTER TABLE employee_roles
        ADD CONSTRAINT fk_employee_role_company
        FOREIGN KEY (company_id) REFERENCES companies(id);
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;

-- Preserve the original roles under their first known company. Roles that are
-- not yet used are attached to the first company created in the installation.
UPDATE employee_roles role
SET company_id = COALESCE(
    (SELECT MIN(employee.company_id)
     FROM employees employee
     WHERE employee.role_id = role.id AND employee.company_id IS NOT NULL),
    (SELECT MIN(company.id) FROM companies company)
)
WHERE role.company_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_employee_role_company_name
    ON employee_roles (company_id, LOWER(name));
CREATE INDEX IF NOT EXISTS idx_employee_role_company
    ON employee_roles (company_id);

-- If a legacy role was shared by employees from several companies, create a
-- company-owned copy and retain every employee's existing assignment.
INSERT INTO employee_roles (name, description, company_id)
SELECT DISTINCT role.name, role.description, employee.company_id
FROM employees employee
JOIN employee_roles role ON role.id = employee.role_id
WHERE employee.company_id IS NOT NULL
  AND role.company_id IS DISTINCT FROM employee.company_id
ON CONFLICT DO NOTHING;

UPDATE employees employee
SET role_id = tenant_role.id
FROM employee_roles original_role,
     employee_roles tenant_role
WHERE original_role.id = employee.role_id
  AND employee.company_id IS NOT NULL
  AND original_role.company_id IS DISTINCT FROM employee.company_id
  AND tenant_role.company_id = employee.company_id
  AND LOWER(tenant_role.name) = LOWER(original_role.name);
