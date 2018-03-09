package spark.es.demo;

/**
 * <pre>
 * Created with IntelliJ IDEA.
 * User: ly
 * Date: 2017/9/19
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author ly
 */
public enum BusinessType {


    /**
     * 影视
     */
    WQ_MOVIES(1),

    /**
     * 小说
     */
    WQ_FICTION(2),

    /**
     * 音乐
     */
    WQ_MUSIC(3),

    /**
     * 漫画
     */
    WQ_CARICATURE(4),

    /**
     *软件
     */
    WQ_SOFTWARE(5),

    /**
     * app
     */
    WQ_APP(6),

    /**
     * 出版物
     */
    WQ_TRADEMARK(7);


    protected final int type;

    BusinessType(int type) {
        this.type = type;
    }

    public static BusinessType valueOf(int type){
        switch (type){
            case 1:
                return WQ_MOVIES;
            case 2:
                return WQ_FICTION;
            case 3:
                return WQ_MUSIC;
            case 4:
                return WQ_CARICATURE;
            case 5:
                return WQ_SOFTWARE;
            case 6:
                return WQ_APP;
            case 7:
                return WQ_TRADEMARK;
            default:
                return WQ_MOVIES;
        }
    }
}
