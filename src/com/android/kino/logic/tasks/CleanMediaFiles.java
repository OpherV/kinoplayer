package com.android.kino.logic.tasks;

import com.android.kino.musiclibrary.Library;

public class CleanMediaFiles extends KinoTask {
	private Library mLibrary;
	private Library.LibraryStatusUpdater mUpdater = new Library.LibraryStatusUpdater(){
		@Override
		public void updateProgress(String progresString) {
			mTaskAction=progresString;
			updateUI();
		}
	};
	
	public CleanMediaFiles(Library library){
		mLibrary=library;
		mTaskTitle="Cleaning media files";
	}	
	
	@Override
	protected void publishProgress(int progress) {		
	}


	@Override
	protected Void doInBackground(Void... arg0) {
		mLibrary.cleanAlbumDir(mUpdater);				
		mLibrary.cleanArtistDir(mUpdater);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {	
		super.onPostExecute(result);
		mTaskMaster.taskDone(this);	
	}

}
