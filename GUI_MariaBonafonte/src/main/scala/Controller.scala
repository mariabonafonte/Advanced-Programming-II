import java.awt.event.{ActionEvent, ActionListener}
import Driver.messages

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class Controller(private val panel: Panel) extends ActionListener with PropertyChangeListener:
  private var task: Task | Null = null

  override def actionPerformed(e: ActionEvent): Unit =
    e.getActionCommand match
      case "START" =>
        try
          panel.clearTextArea()
          val n = panel.getNumber()
          if n<1 || n>1000 then
            panel.showMessage(messages.getString("status") + messages.getString("status_range_error"))
            panel.clearTextField()
          else
            panel.clearTextArea()
            panel.setProgress(0)
            panel.enableStart(false)
            panel.enableCancel(true)
            panel.showMessage(messages.getString("status") + messages.getString("status_working"))
            task = Task(n, panel)
            task.addPropertyChangeListener(this);
            task.execute()

        catch
          case _: NumberFormatException =>
            panel.showMessage(messages.getString("status") + messages.getString("status_invalid_number"))
            panel.clearTextField()

      case "CANCEL" =>
        if task != null then task.cancel(true)
        panel.showMessage(messages.getString("status") + messages.getString("status_cancelling"))

  override def propertyChange (evt: PropertyChangeEvent): Unit =
    if evt.getPropertyName == "progress" then {
      val p = evt.getNewValue.asInstanceOf[Int]
      panel.setProgress(p)
    }