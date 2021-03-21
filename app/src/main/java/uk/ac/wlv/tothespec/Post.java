package uk.ac.wlv.tothespec;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uk.ac.wlv.tothespec.database.ToTheSpecCursorWrapper;
import uk.ac.wlv.tothespec.database.ToTheSpecBaseHelper;
import uk.ac.wlv.tothespec.database.ToTheSpecDbSchema;
public class Post {
     private static Post sPost;
     private List<Spec> mSpec;
     private Context mContext;
     private SQLiteDatabase mDatabase;

        public static Post get(Context context) {
            if (sPost == null) {
                sPost = new Post(context);
            }
            return sPost;
        }
        Post(Context context) {
            mSpec = new ArrayList<>();
            mContext = context.getApplicationContext();
            mDatabase = new ToTheSpecBaseHelper(mContext).getWritableDatabase();
        }

        public List<Spec> getSpecs() {
            List<Spec> specs = new ArrayList<>();
            ToTheSpecCursorWrapper cursor = querySpecs(null, null);
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    specs.add(cursor.getSpec());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            return specs;
        }

        public void addSpec(Spec spec) {
            ContentValues values = getContentValues(spec);
            mDatabase.insert(ToTheSpecDbSchema.SpecTable.NAME, null, values);
        }

        public Spec getSpec(UUID id) {
            ToTheSpecCursorWrapper cursor = querySpecs(
                    ToTheSpecDbSchema.SpecTable.Cols.UUID + " = ?",
                    new String[]{id.toString()}
            );
            try {
                if (cursor.getCount() == 0) {
                    return null;
                }
                cursor.moveToFirst();
                return cursor.getSpec();
            } finally {
                cursor.close();
            }
        }

        public void updateSpec(Spec spec) {
            String uuidString = spec.getId().toString();
            ContentValues values = getContentValues(spec);
            mDatabase.update(ToTheSpecDbSchema.SpecTable.NAME, values,
                    ToTheSpecDbSchema.SpecTable.Cols.UUID + " = ?",
                    new String[]{uuidString});
        }

        public void deleteSpec(Spec spec) {
            String uuidString = spec.getId().toString();
            mDatabase.delete(ToTheSpecDbSchema.SpecTable.NAME,
                    ToTheSpecDbSchema.SpecTable.Cols.UUID + " = ?",
                    new String[]{uuidString});
        }

        private ToTheSpecCursorWrapper querySpecs(String whereClause, String[] whereArgs) {
            Cursor cursor = mDatabase.query(
                    ToTheSpecDbSchema.SpecTable.NAME,
                    null,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    null
            );
            return new ToTheSpecCursorWrapper(cursor);
        }

        private static ContentValues getContentValues(Spec spec) {
            ContentValues values = new ContentValues();
            values.put(ToTheSpecDbSchema.SpecTable.Cols.UUID, spec.getId().toString());
            values.put(ToTheSpecDbSchema.SpecTable.Cols.MESSAGE, spec.getMessage());
            values.put(ToTheSpecDbSchema.SpecTable.Cols.URL, spec.getUrl());
            return values;
        }

}
