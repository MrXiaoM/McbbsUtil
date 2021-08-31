package top.mrxiaom.doomsdayessentials.utils;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * һ���ܼ򵥵Ŀ⣬Ŀǰֻ������ȡ���Ӳ���
 * 
 * @author MrXiaoM
 */
public class McbbsUtil {
	public static enum Operation{
		// �ݲ���������ӭ��ȫ
		CLOSE("�ر�"),
		MOVE("�ƶ�"),
		HIGH_LIGHT("���ø���"),
		ADD_ICON("���ͼ��"),
		ADD_STAMP("���ͼ��"),
		BE_ESSENCED("���뾫��"),
		UP_SERVER("����(������/���״���������)"),
		UP("����(������)"),
		UNKNOWN("[ MrXiaoM \n Product ]");
		String displayText;
		Operation(String displayText){
			this.displayText = displayText;
		}
		public String getDisplayText() {
			return displayText;
		}
		public static Operation getOperation(String value) {
			for(Operation o : Operation.values()) {
				if(o.displayText.contains(value)) {
					return o;
				}
			}
			return Operation.UNKNOWN;
		}
	}
	public static class ThreadOperation {
		String uid;
		String name;
		Operation operation;
		String operationString;
		String time;
		String term;

		public String getUid() {
			return uid;
		}

		public String getName() {
			return name;
		}
		
		public String getOperationString() {
			return operationString;
		}

		public String getTime() {
			return time;
		}

		public String getTerm() {
			return term;
		}

		private ThreadOperation(String uid, String name, String operation, String time, String term) {
			super();
			this.uid = uid;
			this.name = name;
			this.operationString = operation;
			this.operation = Operation.getOperation(operation);
			this.time = time;
			this.term = term;
		}

		@Override
		public String toString() {
			return "thread{uid=" + uid + ",name=" + name + ",time=" + time + ",operation=" + operation.name().toUpperCase() + ", operationString=" + operationString + ",term=" + term
					+ "}";
		}

		public Operation getOperation() {
			return operation;
		}
	}
	public static List<ThreadOperation> getThreadOperation(String tid) throws IOException {
		return getThreadOperation(tid, "UTF-8");
	}
	public static List<ThreadOperation> getThreadOperation(String tid, String charset) throws IOException {
		List<ThreadOperation> list = new ArrayList<>();
		URLConnection connection = new URL("https://www.mcbbs.net/forum.php?mod=misc&action=viewthreadmod&tid=" + tid).openConnection();
		connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Homo/114514.1919810 Edg/92.0.902.78");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
		String line, content = "", uid = "", name = "", time = "", operation = "", term = "";
		while ((line = reader.readLine()) != null) content += line;
		int i, j = 0, l = 0;
		if((i = content.indexOf("<table class=\"list\"")) < 0) return list;
		content = content.substring(i, content.indexOf("</table>", i)).replace("<td >", "<td>");
		i = content.indexOf("<td>");
		while (i >= 0) {
			i += 4;
			if (j > 3) {
				String text = content.substring(i, content.indexOf("</td>", i));
				if (text.contains("<a href=\"") && text.contains("uid=")) {
					// uid �� �û���
					int k = text.indexOf("uid=") + 4;
					uid = text.substring(k, text.indexOf("\"", k));
					name = text.substring(text.indexOf(">") + 1, text.indexOf("</a>"));
					l++;
				} else if (text.contains("<span title=\"")) {
					// ����ʱ�䴦�� (�硰NСʱǰ������ϸʱ���ȡ)
					int k = text.indexOf("<span title=\"") + 13;
					time = text.substring(k, text.indexOf("\"", k));
				} else {
					// ʱ��
					if (l % 5 == 2) time = text;
					// ����
					if (l % 5 == 3) operation = text;
					if (l % 5 == 4) {
						// ʱ��
						term = text;
						list.add(new ThreadOperation(uid, name, operation, time, term));
						uid = name = operation = time = term = "";
					}
				}
				l++;
			}
			i = content.indexOf("<td>", i);
			j++;
		}
		return list;
	}
}
