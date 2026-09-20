import java.awt.*
import java.awt.event.*
import java.util
import java.util.*
import javax.swing.*


class Panel extends JPanel {
  this.setLayout(new BorderLayout)
  private val label = new JLabel("How many friends do you want?")
  private val msg = new JLabel("GUI Created")
  private val number = new JTextField(3)
  private val listOfFriends = new JTextArea(10, 40)
  private val scroll = new JScrollPane(listOfFriends)
  private val end = new JButton("Cancelar")
  private val progress = new JProgressBar(0, 100)
  val north = new JPanel
  north.add(label)
  north.add(number)
  north.add(end)
  this.add(BorderLayout.NORTH, north)
  this.add(BorderLayout.CENTER, scroll)
  val south = new JPanel
  south.add(msg)
  south.add(progress)
  this.add(BorderLayout.SOUTH, south)

  def setController(ctr: ActionListener): Unit = {
  }

  def getNumber = 0

  def writeFriends(list: util.List[Nothing]): Unit = {
  }

  def cleanArea(): Unit = {
  }

  def setMessage(str: String): Unit = {
  }

  def progress(n: Int) = {
  }
}
