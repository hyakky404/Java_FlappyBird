package FlappyBird;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import SQL.Database;
import SQL.Score;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    private final int WIDTH = 800, HEIGHT = 800;
    private JFrame frame;
    private Renderer render;
    private Timer timer;
    private Random rnd;
    private ArrayList<Rectangle> columns;
    private Rectangle bird;
    private Image imgBird;
    private ImageIcon icon;
    private int ticks, motion;
    private static int score;
    private int highScore;
    private boolean startGame, gameOver;

    public static FlappyBird flappyBird;

    public FlappyBird()
    {
    	timer = new Timer(20, this);
        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 40, 40);
        columns = new ArrayList<Rectangle>();
        rnd = new Random();
        render = new Renderer();
        icon = new ImageIcon("./assets/img/bird.png");
        score = 0;

        gameWindow();
        readFile();
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void gameWindow()
    {
        frame = new JFrame();
        frame.setTitle("Flappy Bird");
        frame.setIconImage(new ImageIcon("./assets/ico/bird_icon.png").getImage());
        frame.add(render);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void readFile()
    {
        try
        {
            File f = new File("highest_score.txt");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null)
            {
                highScore = Integer.parseInt(line);
            }

            fr.close();
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            highScore = 0;
        }
    }

    public void writerFile()
    {
        try
        {
            File f = new File("highest_score.txt");
            FileWriter fw = new FileWriter(f);
            String s = String.valueOf(highScore);

            fw.write(s);
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addColumn(boolean start)
    {
    	
        int width = 100;
        int space = 300;
    	int height = 50 + rnd.nextInt(300);
        
    	if (start)
    	{
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
	    }
        else
        {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void jump()
    {   
        if (!startGame)
        {
            startGame = true;
        }
        else if (!gameOver)
        {
            if (motion > 0)
            {
                motion = 0;
            }

            motion -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int speed = 10;
        ticks++;

        if (startGame)
        {
            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }
            
            if (ticks % 2 == 0)
            {
                motion += 2;
            }

            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);

                if (column.x + column.width < 0)
                {
                    columns.remove(column);

                    if (column.y == 0)
                    {
                        addColumn(false);
                    }
                }
            }
                
            bird.y += motion;

            for (Rectangle column: columns)
            {
                if (column.y == 0 && bird.x + bird.width / 4 > column.x + column.width / 2 - 10 && bird.x + bird.width / 4 < column.x + column.width / 2 + 10)
                {
                    score++;
                }

                if (column.intersects(bird))
                {
                    gameOver = true;
                }
            }

            if (bird.y > HEIGHT - 150 || bird.y < 0)
            {
                gameOver = true;
            }
        }

        if (gameOver)
        {
            timer.stop();
            writerFile();

            try
            {
                Database.add(new Score(FlappyBird.getScore()));
            }
            catch (SQLException se)
            {
                se.printStackTrace();
            }
            finally
            {
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Score: " + score + "\n" +
                    "Best: " + highScore +
                    "\nNew Game?", "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, icon);

                if (confirm == JOptionPane.YES_OPTION)
                {
                    frame.dispose();
                    flappyBird = new FlappyBird();
                }
                else
                {
                    System.exit(0);
                }
            }
        }

        render.repaint();
    }

    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void graphics(Graphics g)
    {
        g.setColor(Color.cyan);
	    g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);
            
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);
        
        try
        {
            imgBird = ImageIO.read(new File("./assets/img/bird.png"));
        }
        catch (IOException e)
        {
            g.setColor(Color.orange);
            g.fillRect(bird.x, bird.y, bird.width, bird.height);
        }

        g.setColor(Color.red);
        g.drawImage(imgBird, bird.x, bird.y, bird.width, bird.height, null);

        for (Rectangle column: columns)
        {
            paintColumn(g, column);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", 1, 35));
            
        if (!startGame)
        {
            g.drawString("GET READY!", WIDTH / 2 - 90, HEIGHT / 2 - 50);
        }

        if (!gameOver && startGame)
        {
            if (score > highScore)
            {
                highScore = score;
            }

            g.drawString(String.valueOf(score), WIDTH / 2, 100);
        }
    }

    public static int getScore()
    {
        return score;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            jump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_F)
        {
            jump();
        }

        if (e.getKeyCode() == KeyEvent.VK_F1)
        {
            JOptionPane.showMessageDialog(frame,"Click Left Mouse or press F to jump!");
        }

        if (e.getKeyCode() == KeyEvent.VK_F9)
        {
            JOptionPane.showMessageDialog(frame, "v1.2.5\n201200304\n201210213",
                "About",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
        }

        if (e.getKeyCode() == KeyEvent.VK_END)
        {
            JOptionPane.showMessageDialog(frame, "I have heard about a legless bird. We keep flying, flying. " +
                "When we are tired, we will sleep on the wind.\nThere's only one landing in a lifetime, " +
                "and that's when we die.",
                "Story about the bird",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    public static void main(String[] args) throws Exception
    {
        flappyBird = new FlappyBird();
    }
}
