package sage;

import java.awt.*;
import com.borland.jbcl.layout.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.text.Document;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.*;
import java.util.*;

public class RELTEST1
    extends SageFilePanel
    implements DocumentListener, ActionListener {
  IconNode analysis_node;
  IconNode errorF_node;
  IconNode errorpedigree_node;
  IconNode errorlocus_node;
  IconNode errorgenome_node;
  IconNode pedi_node;
  IconNode locus_node;
  IconNode genome_node;

  XYLayout xYLayout1 = new XYLayout();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JTextField jTextField3 = new JTextField();
  JButton jButton3 = new JButton();
  JTextField jTextField4 = new JTextField();
  JButton jButton4 = new JButton();

  public RELTEST1(sage_analysis_info data, IconNode inputnode,
                  IconNode errornode) {
    Analysis_object = data;
    analysis_node = inputnode;
    errorF_node = errornode;
    setModel(new PropertyDataModel());
    Analysis_object.create_error_folder= true;
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setModel(DataCollectionModel model) {
    this.Datamodel = model;
    Datamodel.setValue("output_name", jTextField4.getText());
  }

  void jbInit() throws Exception {
    NodeInfo outputfolder = new NodeInfo("Output", "OutputFolder", "RELTEST",
                                         Analysis_object, "");
    outputF_node = new IconNode(outputfolder, "OutputFolder");

    this.setLayout(xYLayout1);
    xYLayout1.setWidth(500);
    xYLayout1.setHeight(560);
    jLabel3.setToolTipText(
        "<html>Lists the alleles, allele frequencies and phenotype to genotype mapping " +
        "<br>for each marker locus.");
    jLabel3.setText("Marker locus file");
    jTextFieldPara.addMouseListener(new RELTEST1_jTextField1_mouseAdapter(this));
    jTextFieldPed.addMouseListener(new RELTEST1_jTextField2_mouseAdapter(this));
    jTextField3.setText("");
    jTextField3.addMouseListener(new RELTEST1_jTextField3_mouseAdapter(this));
    jButton3.setText("...");
    jButtonPara.addActionListener(this);
    jButtonPed.addActionListener(this);
    jButton3.setToolTipText("<html>Lists the alleles at each marker locus.");
    jButton3.setText("...");
    jButton3.addActionListener(this);

    jTextField4.setToolTipText(
        "<html>Specifies the name of the output file generated by this analysis.");
    jTextField4.setText("");
    jTextField4.addMouseListener(new RELTEST1_jTextField4_mouseAdapter(this));
    jLabel4.setToolTipText("<html>Contains a description of the linked marker regions, including distances " +
                           "<br>between markers.");
    jLabel4.setText("Genome file");
    jButton4.setText("...");
    jButton4.addActionListener(this);

    OutputNameField.addMouseListener(new RELTEST1_OutputNameField_mouseAdapter(this));
    OutputNameField.setToolTipText(
        "<html>Specifies the name of the output file generated by this analysis.");
    OutputNameField.setText("reltest");
    jNextButton.setHorizontalTextPosition(SwingConstants.LEFT);
    jNextButton.setIcon(error_image);
    jNextButton.setMargin(new Insets(2, 2, 2, 2));
    jNextButton.setText("Next");
    jNextButton.addActionListener(this);

    this.add(jLabelPara, new XYConstraints(20, 20, 142, 20));
    this.add(jLabelPed, new XYConstraints(20, 50, 142, 20));
    this.add(jLabel3, new XYConstraints(20, 80, 142, 20));
    this.add(jLabel4, new XYConstraints(20, 110, 142, 20));
    this.add(jTextFieldPara,  new XYConstraints(155, 20, 280, 20));
    this.add(jTextFieldPed,    new XYConstraints(155, 50, 280, 20));
    this.add(jTextField3,    new XYConstraints(155, 80, 280, 20));
    this.add(jButtonPed,  new XYConstraints(445, 50, 30, 20));
    this.add(jButton3,  new XYConstraints(445, 80, 30, 20));
    this.add(jButtonPara,  new XYConstraints(445, 20, 30, 20));
    this.add(jTextField4,    new XYConstraints(155, 110, 280, 20));
    this.add(jButton4,  new XYConstraints(445, 110, 30, 20));
    this.add(jLabelOutputName, new XYConstraints(20, 150, 142, 20));
    this.add(OutputNameField, new XYConstraints(155, 150, 280, 20));
    this.add(jNextButton, new XYConstraints(420, 520, 60, 25));

    jTextFieldPara.getDocument().addDocumentListener(this);
    jTextFieldPed.getDocument().addDocumentListener(this);
    jTextField3.getDocument().addDocumentListener(this);
    jTextField4.getDocument().addDocumentListener(this);
    OutputNameField.getDocument().addDocumentListener(this);

    Datamodel.setValue("output_name", OutputNameField.getText());
  }

  public void actionPerformed(ActionEvent e) {
    Object ob = e.getSource();
    if (ob == jButtonPara)
      jButton1_actionPerformed();
    else if (ob == jButtonPed)
      jButton2_actionPerformed();
    else if (ob == jButton3)
      jButton3_actionPerformed();
    else if (ob == jButton4)
      jButton4_actionPerformed();
    else if (ob == jNextButton)
      jNextButton_actionPerformed();
  }

  void jNextButton_actionPerformed() {
    MyInternalFrame mf = (MyInternalFrame) Frame1.mainFrame1.activeinframe;
    mf.jTabbedPane1.setSelectedIndex(1);
  }

  void jButton1_actionPerformed() {
    jFileChooser1.setCurrentDirectory(new File(Frame1.mainFrame1.path_forFileChooser));
    jFileChooser1.setFileFilter(ParaFilter);
    jFileChooser1.setDialogTitle("Add Parameter File");

    if (jFileChooser1.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
      String filepath = jFileChooser1.getSelectedFile().getPath();
      String filename = jFileChooser1.getSelectedFile().getName();
      NodeInfo filenode = new NodeInfo(filename, "Parameter File", new File(filepath));
      insertparafile(filenode);
    }
  }

  public void insertparafile(NodeInfo source) {
    File importFile = (File) source.file;
    String FilePath = importFile.getPath();
    Datamodel.setValue("para_path", FilePath);
    jTextFieldPara.setText(FilePath);

    jFileChooser1.setCurrentDirectory(importFile);
    Frame1.mainFrame1.path_forFileChooser = FilePath;

    Analysis_object.para_file_path = FilePath;
    Analysis_object.input_file.add(FilePath);

    if (Analysis_object.create_input_folder == false) {
      NodeInfo nodeinfo = new NodeInfo("Input", "InputFolder", "RELTEST",
                                       Analysis_object, "");
      IconNode node = new IconNode(nodeinfo, "InputFolder");

      inputF_node = addObject(node, analysis_node, false);
      Analysis_object.create_input_folder = true;
    }
    if (Analysis_object.create_para_file_node == false) {
      para_node = new IconNode(source, "Parameter File");

      addObject(para_node, inputF_node, false);
      Analysis_object.create_para_file_node = true;

      treeModel.nodeStructureChanged(errorF_node);

      if (errorF_node.getChildCount() < 1) {
          Analysis_object.create_error_folder= false;
        errorF_node.removeFromParent();
        treeModel.nodeStructureChanged(analysis_node);
        TreePath p = new TreePath(para_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);

        NodeInfo n = (NodeInfo) analysis_node.getUserObject();
        RELTEST2 f2 = (RELTEST2) n.component_vector.get(1);
        f2.jRunButton.setIcon(next_image);
      }
    }
    else {
      para_node.setUserObject(source);
      TreePath p = new TreePath(para_node.getPath());
      Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    }
    TreePath p = new TreePath(analysis_node.getPath());
    Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);

    p = new TreePath(para_node.getPath());
    Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);
    Frame1.mainFrame1.activeinframe.MyTree1.Refresh();
  }

  void jButton2_actionPerformed() {
    jFileChooser1.setFileFilter(FamilyFilter);
    jFileChooser1.setDialogTitle("Add Data File");

    if (jFileChooser1.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
      String filepath = jFileChooser1.getSelectedFile().getPath();
      String filename = jFileChooser1.getSelectedFile().getName();
      NodeInfo filenode = new NodeInfo(filename, "Pedigree File", new File(filepath));
      insertpedigreefile(filenode);
    }
  }

  public void insertpedigreefile(NodeInfo source) {
    File importFile = (File) source.file;
    String FilePath = importFile.getPath();
    Datamodel.setValue("pedi_path", FilePath);
    jTextFieldPed.setText(FilePath);

    jFileChooser1.setCurrentDirectory(importFile);
    Frame1.mainFrame1.path_forFileChooser = FilePath;

    Analysis_object.family_file_path = FilePath;
    Analysis_object.input_file.add(FilePath);

    if (Analysis_object.create_input_folder == false) {
      NodeInfo nodeinfo = new NodeInfo("Input", "InputFolder", "RELTEST",
                                       Analysis_object, "");
      IconNode node = new IconNode(nodeinfo, "InputFolder");

      inputF_node = addObject(node, analysis_node, false);
      Analysis_object.create_input_folder = true;
    }
    if (Analysis_object.create_family_file_node == false) {
      pedi_node = new IconNode(source, "Pedigree File");

      addObject(pedi_node, inputF_node, false);
      Analysis_object.create_family_file_node = true;

      errorpedigree_node.removeFromParent();
      treeModel.nodeStructureChanged(errorF_node);

      if (errorF_node.getChildCount() < 1) {
          Analysis_object.create_error_folder= false;
        errorF_node.removeFromParent();
        treeModel.nodeStructureChanged(analysis_node);
        TreePath p = new TreePath(pedi_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);

        NodeInfo n = (NodeInfo) analysis_node.getUserObject();
        RELTEST2 f2 = (RELTEST2) n.component_vector.get(1);
        f2.jRunButton.setIcon(next_image);
      }
    }
    else {
      pedi_node.setUserObject(source);
      TreePath p = new TreePath(pedi_node.getPath());
      Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    }

    TreePath p = new TreePath(analysis_node.getPath());
    Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    Frame1.mainFrame1.activeinframe.MyTree1.Refresh();
  }

  void jButton3_actionPerformed() {
    jFileChooser1.setFileFilter(MarkerLocusFilter);
    jFileChooser1.setDialogTitle("Add Locus File");

    if (jFileChooser1.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
      String filepath = jFileChooser1.getSelectedFile().getPath();
      String filename = jFileChooser1.getSelectedFile().getName();
      NodeInfo filenode = new NodeInfo(filename, "Marker Locus File", new File(filepath));
      insertlocusfile(filenode);
    }
  }

  public void insertlocusfile(NodeInfo source) {
    File importFile = (File) source.file;
    String FilePath = importFile.getPath();
    Datamodel.setValue("locus_path", FilePath);
    jTextField3.setText(FilePath);

    jFileChooser1.setCurrentDirectory(importFile);
    Frame1.mainFrame1.path_forFileChooser = FilePath;

    Analysis_object.locus_file_path = FilePath;
    Analysis_object.input_file.add(FilePath);

    if (Analysis_object.create_input_folder == false) {
      NodeInfo nodeinfo = new NodeInfo("Input", "InputFolder", "RELTEST",
                                       Analysis_object, "");
      IconNode node = new IconNode(nodeinfo, "InputFolder");

      inputF_node = addObject(node, analysis_node, false);
      Analysis_object.create_input_folder = true;
    }
    if (Analysis_object.create_locus_file_node == false) {
      locus_node = new IconNode(source, "Marker Locus File");

      addObject(locus_node, inputF_node, false);
      Analysis_object.create_locus_file_node = true;

      errorlocus_node.removeFromParent();
      treeModel.nodeStructureChanged(errorF_node);

      if (errorF_node.getChildCount() < 1) {
          Analysis_object.create_error_folder= false;
        errorF_node.removeFromParent();
        treeModel.nodeStructureChanged(analysis_node);
        TreePath p = new TreePath(locus_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);

        NodeInfo n = (NodeInfo) analysis_node.getUserObject();
        RELTEST2 f2 = (RELTEST2) n.component_vector.get(1);
        f2.jRunButton.setIcon(next_image);
      }
    }
    else {
      locus_node.setUserObject(source);
      TreePath p = new TreePath(locus_node.getPath());
      Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    }

    TreePath p = new TreePath(analysis_node.getPath());
    Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    Frame1.mainFrame1.activeinframe.MyTree1.Refresh();
  }

  void jButton4_actionPerformed() {
    jFileChooser1.setFileFilter(GenomeFilter);
    jFileChooser1.setDialogTitle("Add Genome File");

    if (jFileChooser1.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
      String filepath = jFileChooser1.getSelectedFile().getPath();
      String filename = jFileChooser1.getSelectedFile().getName();
      NodeInfo filenode = new NodeInfo(filename, "Genome File", new File(filepath));
      insertgenomefile(filenode);
    }
  }

  public void insertgenomefile(NodeInfo source) {
    File importFile = (File) source.file;
    String FilePath = importFile.getPath();
    Datamodel.setValue("genome_path", FilePath);
    jTextField4.setText(FilePath);

    jFileChooser1.setCurrentDirectory(importFile);
    Frame1.mainFrame1.path_forFileChooser = FilePath;

    Analysis_object.genome_file_path = FilePath;
    Analysis_object.input_file.add(FilePath);

    if (Analysis_object.create_input_folder == false) {
      NodeInfo nodeinfo = new NodeInfo("Input", "InputFolder", "RELTEST",
                                       Analysis_object, "");
      IconNode node = new IconNode(nodeinfo, "InputFolder");

      inputF_node = addObject(node, analysis_node, false);
      Analysis_object.create_input_folder = true;
    }
    if (Analysis_object.create_genome_file_node == false) {
      genome_node = new IconNode(source, "Genome File");

      addObject(genome_node, inputF_node, false);
      Analysis_object.create_genome_file_node = true;

      errorgenome_node.removeFromParent();
      treeModel.nodeStructureChanged(errorF_node);

      if (errorF_node.getChildCount() < 1) {
          Analysis_object.create_error_folder= false;
        errorF_node.removeFromParent();
        treeModel.nodeStructureChanged(analysis_node);
        TreePath p = new TreePath(genome_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);

        NodeInfo n = (NodeInfo) analysis_node.getUserObject();
        RELTEST2 f2 = (RELTEST2) n.component_vector.get(1);
        f2.jRunButton.setIcon(next_image);
      }
    }
    else {
      genome_node.setUserObject(source);
      TreePath p = new TreePath(genome_node.getPath());
      Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    }

    TreePath p = new TreePath(analysis_node.getPath());
    Frame1.mainFrame1.activeinframe.MyTree1.setSelectionPath(p);
    Frame1.mainFrame1.activeinframe.MyTree1.Refresh();
    SetRELTEST2RegionInfo(source);
  }

  void SetPanel2Info(NodeInfo source) {

  }

  void SetRELTEST2RegionInfo(NodeInfo source) {
    NodeInfo n = (NodeInfo) analysis_node.getUserObject();
    RELTEST2 f2 = (RELTEST2) n.component_vector.get(1);

    if (source.infomodel == null) {
      FavoritesPanel.Parse_Genome_File(source);
    }

    DataCollectionModel dm = (DataCollectionModel) source.infomodel;

    String para_list = null;
    CheckableItem[] para_lm = null;

      if(f2.jRegionComboBox.getSelectedItem()!=null)
      {
        para_list = f2.jRegionComboBox.getSelectedItem().toString();
        para_lm = f2.jRegionComboBox.ListData;
      }

    if (dm.hasValue("Region_List")) {
      Vector v = (Vector) dm.getValue("Region_List");
      int length = v.size();
      CheckableItem[] items = new CheckableItem[length];

      int i = 0;
      String list=new String();
      for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
        String temp = e.nextElement().toString().trim();
        VariableData temp2 = new VariableData(temp, "region");
        items[i] = new CheckableItem(temp2);
        if(para_list == null || para_lm == null)// in new analysis, set default
        {
          items[i].setSelected(true);
          list = list + ", " + items[i];
        }
        else
        {
          StringTokenizer st = new StringTokenizer(para_list, ",");
          while (st.hasMoreTokens()) {
            String checkitem = st.nextElement().toString().trim();
            if (items[i].toString().compareTo(checkitem) == 0)
            {
              items[i].setSelected(true);
              list = list + ", " + items[i];
            }
          }
        }
      }
      list = list.replaceFirst(",","");
      list = list.trim();
      f2.jRegionComboBox.setData(items);
      f2.jRegionComboBox.setSelectedItem(list);
    }
  }

  public void changedUpdate(DocumentEvent event) {
    Document document = event.getDocument();
    if (document == jTextFieldPara.getDocument()) {
      Datamodel.setValue("para_path", jTextFieldPara.getText());
    }
    if (document == jTextFieldPed.getDocument()) {
        if(jTextFieldPed.getText().length()>0)
        {
            Datamodel.setValue("pedi_path", jTextFieldPed.getText());
        }
        else
        {
            if(Datamodel.hasValue("pedi_path"))
                Datamodel.removeValue("pedi_path");
        }
    }
    if (document == jTextField3.getDocument()) {
      if(jTextField3.getText().length()>0)
      {
          Datamodel.setValue("locus_path", jTextField3.getText());
      }
      else
      {
          if(Datamodel.hasValue("locus_path"))
              Datamodel.removeValue("locus_path");
      }
    }
    if (document == jTextField4.getDocument()) {
      if(jTextField4.getText().length()>0)
      {
          Datamodel.setValue("genome_path", jTextField4.getText());
      }
      else
      {
          if(Datamodel.hasValue("genome_path"))
              Datamodel.removeValue("genome_path");
      }
    }
    if (document == OutputNameField.getDocument()) {
      Datamodel.setValue("output_name", OutputNameField.getText());
    }

    if (Datamodel.hasValue("para_path") && Datamodel.hasValue("pedi_path")
        && Datamodel.hasValue("locus_path") && Datamodel.hasValue("genome_path") &&
        Datamodel.hasValue("output_name")) {
      jNextButton.setIcon(next_image);
    }
  }

  public void insertUpdate(DocumentEvent event) {
    changedUpdate(event);
  }

  public void removeUpdate(DocumentEvent event) {
    changedUpdate(event);
  }

  void detelePedNode()
  {
    if(errorpedigree_node != null && errorF_node != null)
    {
        addObject(errorpedigree_node, errorF_node, false);
        if(!Analysis_object.create_error_folder)
        {
            treeModel.insertNodeInto(errorF_node, analysis_node, 0);
            Analysis_object.create_error_folder = true;
        }

        Analysis_object.create_family_file_node = false;
        TreePath p = new TreePath(errorpedigree_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);
    }
  }

  void deteleLocusNode()
  {
    if(errorlocus_node != null && errorF_node != null)
    {
        addObject(errorlocus_node, errorF_node, false);
        if(!Analysis_object.create_error_folder)
        {
            treeModel.insertNodeInto(errorF_node, analysis_node, 0);
            Analysis_object.create_error_folder = true;
        }

        Analysis_object.create_locus_file_node = false;
        TreePath p = new TreePath(errorlocus_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);
    }
  }

  void deteleGenomeNode()
  {
    if(errorgenome_node != null && errorF_node != null)
    {
        addObject(errorgenome_node, errorF_node, false);
        if(!Analysis_object.create_error_folder)
        {
            treeModel.insertNodeInto(errorF_node, analysis_node, 0);
            Analysis_object.create_error_folder = true;
        }

        Analysis_object.create_genome_file_node = false;
        TreePath p = new TreePath(errorgenome_node.getPath());
        Frame1.mainFrame1.activeinframe.MyTree1.scrollPathToVisible(p);
    }
  }

  void jTextField1_mouseClicked(MouseEvent e) {
      Frame1.mainFrame1.pdfframe.setTextonPage("parameter fil", false, 223);
  }

  void jTextField2_mouseClicked(MouseEvent e) {
      Frame1.mainFrame1.pdfframe.setTextonPage("pedigree data fil", false, 223);
  }

  void jTextField3_mouseClicked(MouseEvent e) {
      Frame1.mainFrame1.pdfframe.setTextonPage("marker locus description fil", false, 223);
  }

  void jTextField4_mouseClicked(MouseEvent e) {
      Frame1.mainFrame1.pdfframe.setTextonPage("genome description fil", false, 223);
  }

  void OutputNameField_mouseClicked(MouseEvent e) {
      Frame1.mainFrame1.pdfframe.setTextonPage("out ", false, 225);
  }
}

class RELTEST1_jTextField1_mouseAdapter extends java.awt.event.MouseAdapter {
  RELTEST1 adaptee;

  RELTEST1_jTextField1_mouseAdapter(RELTEST1 adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jTextField1_mouseClicked(e);
  }
}

class RELTEST1_jTextField2_mouseAdapter extends java.awt.event.MouseAdapter {
  RELTEST1 adaptee;

  RELTEST1_jTextField2_mouseAdapter(RELTEST1 adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jTextField2_mouseClicked(e);
  }
}

class RELTEST1_jTextField3_mouseAdapter extends java.awt.event.MouseAdapter {
  RELTEST1 adaptee;

  RELTEST1_jTextField3_mouseAdapter(RELTEST1 adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jTextField3_mouseClicked(e);
  }
}

class RELTEST1_jTextField4_mouseAdapter extends java.awt.event.MouseAdapter {
  RELTEST1 adaptee;

  RELTEST1_jTextField4_mouseAdapter(RELTEST1 adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jTextField4_mouseClicked(e);
  }
}

class RELTEST1_OutputNameField_mouseAdapter extends java.awt.event.MouseAdapter {
  RELTEST1 adaptee;

  RELTEST1_OutputNameField_mouseAdapter(RELTEST1 adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.OutputNameField_mouseClicked(e);
  }
}
