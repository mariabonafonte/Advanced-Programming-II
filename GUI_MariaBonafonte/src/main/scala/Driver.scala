import java.util.{Locale, ResourceBundle}
import javax.swing.{JFrame, SwingUtilities, WindowConstants}

///I took the Main skeleton from CV sources/workers/guiSimpleDone

object Driver:
  val messages: ResourceBundle = ResourceBundle.getBundle("MessagesBundle", Locale.UK)

  def createGUI(window: JFrame): Unit =
    val panel = new Panel()
    val controller = new Controller(panel)
    panel.setController(controller)
    window.setContentPane(panel)
    window.setVisible(true)
    window.pack()
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  def main(args: Array[String]): Unit =
    SwingUtilities.invokeLater(() =>
      val window = JFrame(messages.getString("title"))
      window.setBounds(100,100,450,300)
      createGUI(window)
    )
