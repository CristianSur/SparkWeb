package model;

import java.util.ArrayList;
import java.util.List;

public class SimpleIdeaDAO implements IdeaDAO {
    private List<Idea> ideas;

    public SimpleIdeaDAO() {
        ideas = new ArrayList<>();
    }

    @Override
    public boolean add(Idea idea) {
        return ideas.add(idea);
    }

    @Override
    public List<Idea> findAll() {
        return new ArrayList<>(ideas);
    }

    @Override
    public Idea findBySlug(String slug) {
        return ideas.stream()
                .filter(idea -> idea.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
