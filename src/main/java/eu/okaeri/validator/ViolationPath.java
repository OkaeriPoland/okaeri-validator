package eu.okaeri.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class ViolationPath {

    private String field;
    private String element;

    private ViolationPath parentPath;

    public ViolationPath(String field, String element) {
        this(field, element, null);
    }

    public void appendPathRoot(@NonNull ViolationPath append) {
        if (this.parentPath == null) {
            this.parentPath = append;
        } else {
            this.parentPath.appendPathRoot(append);
        }
    }

    public String preparePath() {
        return this.field + (this.element != null ? this.element : "");
    }

    public String prepareFullPath() {
        return (this.parentPath != null ? this.parentPath.preparePath() + " -> " : "") + this.preparePath();
    }

}
