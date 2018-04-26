import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SynonymFinder extends JFrame{

    private Font titleFont;
    private Font italicFont;

    private JPanel mainPanel;
    private JPanel searchPanel;
    private JLabel title;
    private JTextField enterWord;
    private JButton searchButton;
    private JList synonymList;
    private JScrollPane synonymScrollPane;
    private DefaultListModel synonymListModel;

    public static void main(String[] args){

        new SynonymFinder();

    }

    private SynonymFinder(){

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setBackground(Color.yellow);
        this.setTitle("SynonymFinder");

        CompPlacement placer = new CompPlacement();

        titleFont = new Font("Times New Roman", Font.BOLD, 80);
        italicFont = new Font("Times New Roman", Font.ITALIC, 25);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(Color.lightGray);
        this.add(mainPanel);

        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setBackground(Color.lightGray);

        title = new JLabel("SynonymFinder");
        title.setFont(titleFont);

        enterWord = new JTextField("Enter a word to find synonyms", 20);
        enterWord.setFont(italicFont);
        enterWord.addFocusListener(new SearchFocusListener());
        enterWord.addKeyListener(new SearchActionKeyListener());

        searchButton = new JButton("Go!");
        searchButton.setFont(italicFont);
        searchButton.addActionListener(new SearchActionButtonListener());

        synonymListModel = new DefaultListModel();

        synonymList = new JList(synonymListModel);
        synonymList.setVisibleRowCount(20);

        synonymScrollPane = new JScrollPane(synonymList);

        placer.addComp(mainPanel, title, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE);
        placer.addComp(mainPanel, searchPanel, 1, 2, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE);
        placer.addComp(searchPanel, enterWord, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE);
        placer.addComp(searchPanel, searchButton, 2, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE);
        placer.addComp(mainPanel, synonymScrollPane, 1, 3, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.NONE);

        this.setVisible(true);

    }

    private class CompPlacement{

        public void addComp(JPanel panel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight,
                            int place, int stretch){

            GridBagConstraints constraints = new GridBagConstraints();

            constraints.gridx = xPos;
            constraints.gridy = yPos;
            constraints.gridwidth = compWidth;
            constraints.gridheight = compHeight;
            constraints.weightx = 100;
            constraints.weighty = 100;
            constraints.insets = new Insets(5, 5, 5, 5);
            constraints.anchor = place;
            constraints.fill = stretch;

            panel.add(comp, constraints);

        }

    }

    private void getSynonyms(){

        String text = enterWord.getText();
        synonymListModel.removeAllElements();

        try {

            Document doc = Jsoup.connect("http://www.thesaurus.com/browse/" + text + "?s=t").get();
            Elements links = doc.select("a[href]");
            for(Element element: links){

                String linkstring = element.attr("href");
                if(linkstring.contains("http://www.thesaurus.com/browse/")){

                    String formattedSynonym = linkstring.replaceAll("http://www.thesaurus.com/browse/",
                            "");
                    formattedSynonym = formattedSynonym.replaceAll("%20", " ");
                    formattedSynonym = formattedSynonym.replaceAll("%27", "'");
                    synonymListModel.addElement(formattedSynonym);

                }

            }

        } catch(Exception E){

            E.printStackTrace();
            synonymListModel.addElement("No synonyms found");

        }

    }

    private class SearchActionButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e){

            getSynonyms();

        }

    }

    private class SearchActionKeyListener implements KeyListener{

        @Override
        public void keyPressed(KeyEvent e){

            if(e.getKeyCode() == KeyEvent.VK_ENTER){

                getSynonyms();

            }

        }

        public void keyReleased(KeyEvent e){}
        public void keyTyped(KeyEvent e){}

    }

    private class SearchFocusListener extends FocusAdapter{

        @Override
        public void focusGained(FocusEvent e){

            enterWord.selectAll();

        }

    }

}
