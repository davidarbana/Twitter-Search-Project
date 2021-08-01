package ui.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import query.Query;
import ui.Application;
import ui.ContentPanel;
import ui.QueryConfiguration;

import javax.swing.*;

import java.awt.event.MouseAdapter;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
    private QueryConfiguration queryConfiguration;
    private ContentPanel contentPanel;
    private Application app;

    @BeforeEach
    void setUp() {
        app = new Application();
        contentPanel = new ContentPanel(app);
        queryConfiguration = new QueryConfiguration(contentPanel);
    }

    @Test
    void mapValid() {
        JMapViewer jmv = contentPanel.getViewer();
        assertTrue(jmv.isEnabled());
    }

    @Test
    void mapInvalid() {
        contentPanel = null;
        try{
            JMapViewer jmv = contentPanel.getViewer();
            assertFalse(jmv.isEnabled());
        }
        catch (NullPointerException nullException){
            System.out.println("Success");
        }
        catch (Exception e){
            System.out.println("Not expected exception ");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void updateVisibility() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread started running...");
                for (Query query : queryConfiguration) {
                    JCheckBox box = query.getCheckBox();
                    assertTrue(box.isValid());

                    Boolean state = box.isSelected();
                    query.setVisible(state);

                    if(state.booleanValue()){
                        assertTrue(query.getVisible());
                    }else{
                        assertFalse(query.getVisible());
                    }
                }
            }
        });

    }
}