package uk.ac.wlv.tothespec.database;
import uk.ac.wlv.tothespec.Spec;

import android.database.Cursor;
import android.database.CursorWrapper;
import java.util.UUID;

public class ToTheSpecCursorWrapper extends CursorWrapper {
    public ToTheSpecCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Spec getSpec() {
        String uuidString = getString(getColumnIndex(ToTheSpecDbSchema.SpecTable.Cols.UUID));
        String message = getString(getColumnIndex(ToTheSpecDbSchema.SpecTable.Cols.MESSAGE));
        String urlString = getString(getColumnIndex(ToTheSpecDbSchema.SpecTable.Cols.URL));
        Spec spec = new Spec(UUID.fromString(uuidString));
        spec.setMessage(message);
        spec.setUrl(urlString);
        return spec;
    }
}
