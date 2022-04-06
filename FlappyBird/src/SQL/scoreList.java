package SQL;

public class scoreList
{
    private int score;

    public scoreList()
    {
    }
    
    public scoreList(int _score)
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