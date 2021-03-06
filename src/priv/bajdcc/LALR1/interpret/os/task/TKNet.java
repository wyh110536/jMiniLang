package priv.bajdcc.LALR1.interpret.os.task;

import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.util.ResourceLoader;

/**
 * 【服务】网络
 *
 * @author bajdcc
 */
public class TKNet implements IOSCodePage {
	@Override
	public String getName() {
		return "/task/net";
	}

	@Override
	public String getCode() {
		return ResourceLoader.load(getClass());
	}
}
