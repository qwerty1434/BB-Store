package kr.bb.store.util.luascript;

public interface RedisLuaScriptExecutor {
    Object execute(String script, String key, Object... args);
}
