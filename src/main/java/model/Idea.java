package model;

import com.github.slugify.Slugify;

import java.util.*;

public class Idea {
    private String slug;
    private String title;
    private String creator;
    private Set<String> voters;



    public Idea(String title, String creator) {
        voters = new HashSet<>();
        this.title = title;
        this.creator = creator;
        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getSlug() {
        return slug;
    }

    public boolean addVoter(String voterUserName) {
        return voters.add(voterUserName);
    }

    public List<String> getVoters() {
        return new ArrayList<>(voters);
    }

    public int getVoteCount() {
        return voters.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Idea that = (Idea) o;
        return Objects.equals(title, that.title) && Objects.equals(creator, that.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, creator);
    }
}
