
package osproject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<GanttChart> ganttData = new ArrayList<>();
    private final int SCALE = 40;        
    private final int BAR_HEIGHT = 50;
    private final int TOP_MARGIN = 40;

    public void setGanttData(List<GanttChart> data) {
        this.ganttData = data;
        if (!data.isEmpty()) {
            int maxEnd = 0;
            for (GanttChart g : data) {
                if (g.end > maxEnd) maxEnd = g.end;
            }
            setPreferredSize(new Dimension(maxEnd * SCALE + 100, 150));
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (ganttData == null || ganttData.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("No execution data yet.", 20, 50);
            return;
        }

        for (int i = 0; i < ganttData.size(); i++) {
            GanttChart entry = ganttData.get(i);
            
            // Calculate X based strictly on logical cycles
            int drawX = (entry.start * SCALE) + 20;
            int width = (entry.end - entry.start) * SCALE;

            // 1. Draw the Process Block
            g2.setColor(new Color(70, 130, 180));
            g2.fillRect(drawX, TOP_MARGIN, width, BAR_HEIGHT);
            
            // 2. Draw the Outline
            g2.setColor(Color.BLACK);
            g2.drawRect(drawX, TOP_MARGIN, width, BAR_HEIGHT);

            // 3. Draw PID Label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int pidX = drawX + (width - fm.stringWidth(entry.pid)) / 2;
            g2.drawString(entry.pid, pidX, TOP_MARGIN + (BAR_HEIGHT / 2) + 5);

            // 4. Draw Time Markers (Logic to prevent duplication)
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            
            // ONLY draw the Start time for the very first process in the list
            if (i == 0) {
                g2.drawString(String.valueOf(entry.start), drawX, TOP_MARGIN + BAR_HEIGHT + 20);
            }
            
            // ALWAYS draw the End time (this acts as the start for the next block)
            g2.drawString(String.valueOf(entry.end), drawX + width - 5, TOP_MARGIN + BAR_HEIGHT + 20);
        }

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        g2.drawString("Timeline (Logical Cycles)", 20, TOP_MARGIN - 10);
    }
}