package ua.org.gostroy.jhipsterapp.domain;

import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Entry.
 */

@Document(collection = "entry")
public class Entry implements Serializable {

    @Id
    private String id;

    @Field("title")
    private String title;
    
    @Field("content")
    private String content;
    
    @NotNull
    @Field("date")
    private ZonedDateTime date;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entry entry = (Entry) o;
        if(entry.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, entry.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Entry{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", content='" + content + "'" +
            ", date='" + date + "'" +
            '}';
    }
}
