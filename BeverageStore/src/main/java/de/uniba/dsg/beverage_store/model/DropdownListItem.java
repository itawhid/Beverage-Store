package de.uniba.dsg.beverage_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropdownListItem<T> {
    private T id;
    private String label;
    private String description;
}
