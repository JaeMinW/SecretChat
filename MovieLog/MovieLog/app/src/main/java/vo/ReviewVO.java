package vo;

public class ReviewVO {
    int user_idx, review_idx, rating_bar;
    String m_title, m_director, m_date, img, review_title, et_review;

    int resources;

    public ReviewVO(int resources){
        this.resources = resources;
    }

    public int getUser_idx() {
        return user_idx;
    }

    public void setUser_idx(int user_idx) {
        this.user_idx = user_idx;
    }

    public int getReview_idx() {
        return review_idx;
    }

    public void setReview_idx(int review_idx) {
        this.review_idx = review_idx;
    }

    public int getRating_bar() {
        return rating_bar;
    }

    public void setRating_bar(int rating_bar) {
        this.rating_bar = rating_bar;
    }

    public String getM_title() {
        return m_title;
    }

    public void setM_title(String m_title) {
        this.m_title = m_title;
    }

    public String getM_director() {
        return m_director;
    }

    public void setM_director(String m_director) {
        this.m_director = m_director;
    }

    public String getM_date() {
        return m_date;
    }

    public void setM_date(String m_date) {
        this.m_date = m_date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getReview_title() {
        return review_title;
    }

    public void setReview_title(String review_title) {
        this.review_title = review_title;
    }

    public String getEt_review() {
        return et_review;
    }

    public void setEt_review(String et_review) {
        this.et_review = et_review;
    }
}
