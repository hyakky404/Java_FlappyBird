package SQL;

public class ScoreList
{
    private int score;

    public ScoreList() { }
    
    public ScoreList(int _score)
    {
        score = _score;
    }

    public int getScore() 
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }
}
