package priv.bajdcc.LALR1.interpret.module;

import priv.bajdcc.LALR1.grammar.Grammar;
import priv.bajdcc.LALR1.grammar.runtime.*;
import priv.bajdcc.LALR1.ui.drawing.UIGraphics;
import priv.bajdcc.util.ResourceLoader;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 【模块】界面
 *
 * @author bajdcc
 */
public class ModuleUI implements IInterpreterModule {

	private static final int INPUT_TIME = 10;
	private static ModuleUI instance = new ModuleUI();
	private UIGraphics graphics;
	private Queue<Character> queue = new LinkedBlockingDeque<>(1024);
	private Queue<Character> queueDisplay = new ArrayDeque<>();
	private StringBuilder sb = new StringBuilder();
	private RuntimeCodePage runtimeCodePage;

	public void setGraphics(UIGraphics graphics) {
		this.graphics = graphics;
	}

	public void addInputChar(char c) {
		queue.add(c);
	}

	public void addDisplayChar(char c) {
		queueDisplay.add(c);
	}

	public static ModuleUI getInstance() {
		return instance;
	}

	@Override
	public String getModuleName() {
		return "sys.ui";
	}

	@Override
	public RuntimeCodePage getCodePage() throws Exception {
		if (runtimeCodePage != null)
			return runtimeCodePage;

		String base = ResourceLoader.load(getClass());

		Grammar grammar = new Grammar(base);
		RuntimeCodePage page = grammar.getCodePage();
		IRuntimeDebugInfo info = page.getInfo();
		buildUIMethods(info);

		return runtimeCodePage = page;
	}

	private void buildUIMethods(IRuntimeDebugInfo info) {
		info.addExternalFunc("g_ui_print_internal", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "显示输出";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kChar};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				graphics.drawText((char) args.get(0).getObj());
				return null;
			}
		});
		info.addExternalFunc("g_ui_input_internal", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "显示输入";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				status.getService().getProcessService().sleep(status.getPid(), INPUT_TIME);
				Character c = queue.poll();
				if (c == null) {
					return null;
				}
				if (c.equals('\n')) {
					String str = sb.toString();
					sb = new StringBuilder();
					queueDisplay.clear();
					return new RuntimeObject(str);
				} else if (c.equals('\b')) {
					if (sb.length() > 0)
						sb.deleteCharAt(sb.length() - 1);
					graphics.drawText('\b');
					return null;
				} else {
					if (c < '\ufff0') {
						sb.append(c);
					}
					queueDisplay.add(c);
				}
				return null;
			}
		});
		info.addExternalFunc("g_ui_input_im", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "立即显示输入";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				status.getService().getProcessService().sleep(status.getPid(), INPUT_TIME);
				String str = sb.toString();
				sb = new StringBuilder();
				queueDisplay.clear();
				return new RuntimeObject(str);
			}
		});
		info.addExternalFunc("g_ui_input_queue", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "显示输入缓冲";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				status.getService().getProcessService().sleep(status.getPid(), INPUT_TIME);
				String str = String.valueOf(args.get(0).getObj());
				sb.append(str);
				for (char c : str.toCharArray()) {
					queueDisplay.add(c);
				}
				return null;
			}
		});
		info.addExternalFunc("g_ui_print_input", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "实时显示输入";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				return new RuntimeObject(queueDisplay.poll());
			}
		});
		info.addExternalFunc("g_ui_caret", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "设置光标闪烁";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kBool};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				boolean caret = (boolean) args.get(0).getObj();
				if (caret) {
					graphics.setCaret(true);
					return null;
				} else {
					graphics.setCaret(false);
					status.getService().getProcessService().sleep(status.getPid(), INPUT_TIME);
					return new RuntimeObject(graphics.isHideCaret());
				}
			}
		});
		info.addExternalFunc("g_ui_fallback", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "撤销上次输入";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				sb = new StringBuilder();
				graphics.fallback();
				return null;
			}
		});
	}
}
