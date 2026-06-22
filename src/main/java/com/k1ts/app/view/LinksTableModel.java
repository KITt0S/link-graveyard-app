package com.k1ts.app.view;

import com.k1ts.app.model.Link;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LinksTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Domain",
            "Copies",
            "Opened",
            "URL"
    };

    private List<Link> links;

    public LinksTableModel() {
        this.links = new ArrayList<>();
    }

    public void setLinks(List<Link> links) {
        this.links = new ArrayList<>(links);
        fireTableDataChanged();
    }

    public Link getLinkAt(int rowIndex) {
        return links.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return links.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Link link = links.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> link.getDomain();
            case 1 -> link.getCopyCount();
            case 2 -> link.getOpenCount();
            case 3 -> link.getUrl();
            default -> "";
        };
    }
}