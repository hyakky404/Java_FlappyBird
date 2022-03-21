package flappybird;

import java.awt.Graphics;
import javax.swing.JPanel;

public class Render extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        FlappyBird.flappyBird.scene(g);
    }
}