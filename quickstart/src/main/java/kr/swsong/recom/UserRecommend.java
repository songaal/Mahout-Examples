package kr.swsong.recom;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by swsong on 17. 9. 26..
 */
public class UserRecommend {

    private static Logger logger = LoggerFactory.getLogger(UserRecommend.class);

    public UserRecommend() throws IOException {

    }

    public void process() throws IOException, TasteException {
        String fileName = "ratings.csv";
        DataModel dm = getDataModel(fileName);
        //유사도 모델
        UserSimilarity sim = new PearsonCorrelationSimilarity(dm);
        //모든 유저들로 유터 특정 임계값을 충족하는 이웃확인
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, sim, dm);
        //사용자 추천기.
        UserBasedRecommender recommender = new GenericUserBasedRecommender(dm, neighborhood, sim);

        int x = 1;

        int requestItemSize = 5;
        LongPrimitiveIterator iterator = dm.getUserIDs();
        while(iterator.hasNext()) {
            long userId = iterator.nextLong();
            //현재 유저 id에 해당하는 5개 아이템 추천.
            List<RecommendedItem> itemList = recommender.recommend(userId, requestItemSize);
            logger.info("{}] {} =========", x++, userId);
            for(RecommendedItem item : itemList) {
                logger.info("> {} : {}", item.getValue(), item.getItemID());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new UserRecommend().process();
    }

    private DataModel getDataModel(String fileName) throws IOException {
        String filePath = getClass().getClassLoader().getResource(fileName).getFile();
        DataModel dm = new FileDataModel(new File(filePath));
        return dm;
    }
}
