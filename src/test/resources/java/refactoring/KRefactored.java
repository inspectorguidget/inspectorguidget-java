
package java.refactoring;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JMenuItem;
class K {
	JButton but1;
	JButton but2;
	JMenuItem menu1;
	JMenuItem menu2;
	K() {
		but1 = new JButton("foo1");
		but1.addActionListener((ActionEvent e) -> System.out.println("coucou1"));
		menu1 = new JMenuItem("foo1");
		menu1.addActionListener((ActionEvent e) -> System.out.println("coucou1"));
		but2 = new JButton("foo2");
		but2.addActionListener((ActionEvent e) -> System.out.println("coucou2"));
		menu2 = new JMenuItem("foo2");
		menu2.addActionListener((ActionEvent e) -> System.out.println("coucou2"));
	}
}

