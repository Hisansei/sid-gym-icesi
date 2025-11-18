CREATE OR REPLACE FUNCTION notify_user_change() RETURNS TRIGGER AS $$
DECLARE
payload JSON;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.username IS DISTINCT FROM NEW.username) THEN
        payload := json_build_object(
            'operation', 'UPDATE_USERNAME',
            'old_username', OLD.username,
            'new_username', NEW.username
        );

        PERFORM pg_notify('user_updates', payload::text);
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS users_sync_trigger ON USERS;

CREATE TRIGGER users_sync_trigger
    AFTER UPDATE ON USERS
    FOR EACH ROW
    EXECUTE FUNCTION notify_user_change();
