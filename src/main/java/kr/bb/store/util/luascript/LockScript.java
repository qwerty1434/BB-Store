package kr.bb.store.util.luascript;

public class LockScript {
    /*
     * 모든 쿠폰은 expirationDate 설정을 위해 생성 시점에 DUMMY_DATA를 넣어 redis에 등록됩니다.
     * 결과적으로 하나의 데이터(DUMMY_DATA)가 더 들어있기 때문에 이를 고려해 '<='가 아닌 '<'로 개수를 비교해야
     * 쿠폰을 생성할 때 설정한 limitCnt수 만큼 발급받게 할 수 있습니다.
     */
    public static final String script = "local key = KEYS[1]\n" +
            "local value = ARGV[1]\n" +
            "local limitCnt = tonumber(ARGV[2])\n" +
            "local currentCnt = redis.call('SCARD', key)\n" +
            "if currentCnt <= limitCnt then\n" +
            "    redis.call('SADD', key, value)\n" +
            "    return true\n" +
            "else\n" +
            "    return false\n" +
            "end";
}
