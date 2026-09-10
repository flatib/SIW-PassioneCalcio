package it.uniroma3.siw.model.dto;

public class TeamOption {
	private Long id;
    private String name;

    public TeamOption(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
