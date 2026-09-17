import java.awt.BorderLayout
import java.awt.event.ActionListener
import javax.swing.*
import javax.swing.border.EmptyBorder
import Driver.messages

class Panel extends JPanel:
  private val contentPane = JPanel()
  setBorder(new EmptyBorder(10,10,10,10)) // I doubled the border because it was too cramped and looked bad

  private val lblNewLabel = JLabel(messages.getString("quantity"))
  lblNewLabel.setBounds(35,9,54,14)
  lblNewLabel.setToolTipText(messages.getString("quantity_tooltip"))

  private val tfldQuantity = new JTextField()
  tfldQuantity.setBounds(94,6,86,20)
  tfldQuantity.setActionCommand("START")
  tfldQuantity.setColumns(10)
  tfldQuantity.setText("10")

  private val btnStart = JButton(messages.getString("start"))
  btnStart.setBounds(200, 5, 80, 23)
  btnStart.setActionCommand("START")
  btnStart.setToolTipText(messages.getString("start_tooltip"))

  private val btnCancel = JButton(messages.getString("cancel"))
  btnCancel.setBounds(295, 5, 100, 23)
  btnCancel.setActionCommand("CANCEL")
  btnCancel.setToolTipText(messages.getString("cancel_tooltip"))
  btnCancel.setEnabled(false)

  private val lblResult = JLabel(messages.getString("results"))
  lblResult.setBounds(35, 33, 70, 14)

  private val scrollPane = new JScrollPane()
  scrollPane.setBounds(35,52, 359, 147)
  scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS)

  private val tpanelResults = new JTextPane()
  tpanelResults.setEditable(false) //I added this as improvement
  tpanelResults.setToolTipText(messages.getString("results_tooltip"))
  scrollPane.setViewportView(tpanelResults)
  private var result_logPrinted =false //boolean for task implementation

  private val lblProgress = JLabel(messages.getString("progress"))
  lblProgress.setBounds(35, 205, 74, 14)

  private val progressBar = new JProgressBar()
  progressBar.setBounds(107, 204, 287, 17)
  progressBar.setStringPainted(true)

  private val lblStatus = JLabel(messages.getString("status"))
  lblStatus.setBounds(5, 242, 419, 14)
  lblStatus.setHorizontalAlignment(SwingConstants.LEFT)

  setLayout(BorderLayout())

  //I split the panel into subpanels for better organization
  private val topPanel = JPanel()
  topPanel.add(lblNewLabel)
  topPanel.add(tfldQuantity)
  topPanel.add(btnStart)
  topPanel.add(btnCancel)
  add(topPanel, BorderLayout.NORTH)
  
  private val centralPanel = JPanel()
  centralPanel.setLayout(new BorderLayout())
  centralPanel.setBorder(new EmptyBorder(20,20,20,20))
  centralPanel.add(lblResult, BorderLayout.NORTH)
  centralPanel.add(scrollPane, BorderLayout.CENTER)

  private val progressPanel = JPanel()
  progressPanel.setLayout(new BorderLayout())
  progressPanel.add(lblProgress, BorderLayout.WEST)
  progressPanel.add(progressBar, BorderLayout.CENTER)

  centralPanel.add(progressPanel, BorderLayout.SOUTH)

  add(centralPanel, BorderLayout.CENTER)
  add(lblStatus, BorderLayout.SOUTH)

  def setController(ctrl: ActionListener): Unit =
    btnStart.addActionListener(ctrl)
    btnCancel.addActionListener(ctrl)

  def showMessage(text: String): Unit =
    lblStatus.setText(text)

  def clearTextField(): Unit =
    tfldQuantity.setText("")


  def getNumber(): Int =
    tfldQuantity.getText.toInt

  def appendResult(n1: Int, n2: Int, gcd: Int): Unit = 
    if !result_logPrinted  then 
      tpanelResults.setText(tpanelResults.getText() + messages.getString("results_log"))
      result_logPrinted = true
    
    tpanelResults.setText(tpanelResults.getText() + s"$n1, $n2 ... $gcd\n")

  def clearTextArea(): Unit =
    tpanelResults.setText("")
    result_logPrinted = false

  def enableCancel(enable: Boolean): Unit =
    btnCancel.setEnabled(enable)
  
  def enableStart(enable: Boolean): Unit =
    btnStart.setEnabled(enable)

  def setProgress(value: Int): Unit =
    progressBar.setValue(value)

