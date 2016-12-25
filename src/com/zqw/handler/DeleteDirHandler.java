package com.zqw.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.zqw.Activator;
import com.zqw.ExportClassPage;
import com.zqw.FileUtil;

public class DeleteDirHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IProject project = ExportClassHandler.getCurrentProject();
			if (project == null) {
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
				MessageDialog.openInformation(window.getShell(), "ExportClass", "请选择指定项目以清空指定项目的导出class目录");
				return null;
			}
			IPreferenceStore ps = Activator.getDefault().getPreferenceStore();
			String WEB_DEST_PATH = project.getLocation().toOSString() + File.separator + ExportClassPage.webDestPath(ps);
			FileUtil.clearFiles(WEB_DEST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
