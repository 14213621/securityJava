/*
 * JPass
 *
 * Copyright (c) 2009-2017 Gabor Bata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package guiaction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import guilayout.GeneratePasswordDialog;
import guilayout.PVSFrame;
import guilayout.MessageDialog;
//import jpass.ui.JPassFrame;
import jpass.ui.helper.EntryHelper;
import jpass.xml.bind.Entry;

import static javax.swing.KeyStroke.getKeyStroke;
import static jpass.ui.helper.FileHelper.exportFile;
import static jpass.ui.helper.FileHelper.importFile;
import static jpass.ui.helper.FileHelper.openFile;
import static jpass.ui.helper.FileHelper.saveFile;
import static jpass.ui.helper.FileHelper.createNew;
import static guilayout.MessageDialog.getIcon;
import static java.awt.event.InputEvent.CTRL_MASK;
import static java.awt.event.InputEvent.ALT_MASK;

/**
 * Enumeration which holds menu actions and related data.
 *
 * @author Gabor_Bata
 *
 */
public enum MenuActionType {
    NEW_FILE(new AbstractMenuAction("New", getKeyStroke(KeyEvent.VK_N, CTRL_MASK)) {
        private static final long serialVersionUID = -8823457568905830188L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("New File");
           createNew(PVSFrame.getInstance());
        }
    }),
    OPEN_FILE(new AbstractMenuAction("Open File.", getKeyStroke(KeyEvent.VK_O, CTRL_MASK)) {
        private static final long serialVersionUID = -441032579227887886L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Open File");
           openFile(PVSFrame.getInstance());
        }
    }),
    SAVE_FILE(new AbstractMenuAction("Save", getKeyStroke(KeyEvent.VK_S, CTRL_MASK)) {
        private static final long serialVersionUID = 8657273941022043906L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Save");
            saveFile(PVSFrame.getInstance(), false);
        }
    }),
    SAVE_AS_FILE(new AbstractMenuAction("Save As", null) {
        private static final long serialVersionUID = 1768189708479045321L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Save As");

           saveFile(PVSFrame.getInstance(), true);
        }
    }),
    
    
    CHANGE_PASSWORD(new AbstractMenuAction("Change Password", null) {
        private static final long serialVersionUID = 616220526614500130L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Change Password");

        	PVSFrame parent = PVSFrame.getInstance();
            byte[] password = MessageDialog.showPasswordDialog(parent, true);
            if (password == null) {
                MessageDialog.showInformationMessage(parent, "Password has not been modified.");
            } else {
                parent.getModel().setPassword(password);
                parent.getModel().setModified(true);
                parent.refreshFrameTitle();
                MessageDialog.showInformationMessage(parent,
                        "Password has been successfully modified.\n\nSave the file now in order to\nget the new password applied.");
            }
        }
    }),
    GENERATE_PASSWORD(new AbstractMenuAction("Generate Password", getKeyStroke(KeyEvent.VK_Z, CTRL_MASK)) {
        private static final long serialVersionUID = 2865402858056954304L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Generate pasword");
          new GeneratePasswordDialog(PVSFrame.getInstance());
        }
    }),
    
    ENCRPT_METHOD(new AbstractMenuAction("Encrpt Method", getKeyStroke(KeyEvent.VK_M, CTRL_MASK)) {
        private static final long serialVersionUID = 286540285805695212L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Edit Encrpt Method");
        }
    }),
    
    EXIT(new AbstractMenuAction("Exit", getKeyStroke(KeyEvent.VK_F4, ALT_MASK)) {
        private static final long serialVersionUID = -2741659403416846295L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("System exit");
        	PVSFrame.exitFrame();
        }
    }),

    ADD_ENTRY(new AbstractMenuAction("Create User", getKeyStroke(KeyEvent.VK_C, CTRL_MASK)) {
        private static final long serialVersionUID = 6793989246928698613L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Create a users");

			EntryHelper.addEntry(PVSFrame.getInstance());
        }
    }),
    EDIT_ENTRY(new AbstractMenuAction("Edit User Information", getKeyStroke(KeyEvent.VK_E, CTRL_MASK)) {
        private static final long serialVersionUID = -3234220812811327191L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Edit this users");

            EntryHelper.editEntry(PVSFrame.getInstance());
        }
    }),

    DELETE_ENTRY(new AbstractMenuAction("Delete Selected User", getKeyStroke(KeyEvent.VK_D, CTRL_MASK)) {
        private static final long serialVersionUID = -1306116722130641659L;

        @Override
        public void actionPerformed(ActionEvent ev) {
        	System.out.print("Delete this users");
            EntryHelper.deleteEntry(PVSFrame.getInstance());
        }
    });
    
    private final String name;
    private final AbstractMenuAction action;

    private MenuActionType(AbstractMenuAction action) {
        this.name = String.format("jpass.menu.%s_action", this.name().toLowerCase());
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public AbstractMenuAction getAction() {
        return this.action;
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) this.action.getValue(Action.ACCELERATOR_KEY);
    }

    public static final void bindAllActions(JComponent component) {
        ActionMap actionMap = component.getActionMap();
        InputMap inputMap = component.getInputMap();
        for (MenuActionType type : values()) {
            actionMap.put(type.getName(), type.getAction());
            KeyStroke acc = type.getAccelerator();
            if (acc != null) {
                inputMap.put(type.getAccelerator(), type.getName());
            }
        }
    }
}
