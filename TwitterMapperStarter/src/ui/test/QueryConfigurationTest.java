package ui.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import query.Query;
import twitter.LiveTwitterSource;
import twitter.TwitterSource;
import ui.ContentPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QueryConfigurationTest {
    private List<Query> queries = new ArrayList<>();
    private TwitterSource twitterSource;
    private ContentPanel contentPanel;
    private Set<String> ans;
    private Query query;
    private JMapViewer map;


    @BeforeEach
    void setUp() {
        this.contentPanel = contentPanel;
        twitterSource = new LiveTwitterSource();
        query = new Query("Sample", Color.red,map);
        queries.add(query);
        map = new JMapViewer();
        ans = new HashSet<>();
        ans.add("Term1");
        ans.add("Term2");
    }

    @Test
    void addQuery() {
        assertTrue(queries.contains(query));
        assertTrue(query.getColor().equals(Color.red));
        assertTrue(query.getVisible());

        Set<String> allterms = ans;
        twitterSource.setFilterTerms(allterms);
        assertTrue(twitterSource.getFilterTerms().size() == 2);

        twitterSource.addObserver(query);
        assertTrue(twitterSource.countObservers() == 1);
    }

    @Test
    void addQueryInvalid() {
        assertFalse(!queries.contains(query));
        assertFalse(query.getColor().equals(Color.blue));
        assertFalse(!query.getVisible());

        Set<String> allterms = ans;
        twitterSource.setFilterTerms(allterms);
        assertFalse(twitterSource.getFilterTerms().size() != 2);

        twitterSource.addObserver(query);
        assertFalse(twitterSource.countObservers() != 1);
    }

    @Test
    void terminateQuery() {
        twitterSource.addObserver(query);
        assertTrue(query.getVisible());
        query.terminate();

        assertFalse(query.getVisible());
        assertTrue(queries.remove(query));

        Set<String> allterms = ans;
        twitterSource.setFilterTerms(allterms);
        assertTrue(twitterSource.getFilterTerms().size() == 2);

        assertTrue(twitterSource.countObservers() == 1);
        twitterSource.deleteObserver(query);
        assertTrue(twitterSource.countObservers() == 0);
    }
}