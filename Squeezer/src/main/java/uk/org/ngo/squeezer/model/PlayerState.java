/*
 * Copyright (c) 2009 Google Inc.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.ngo.squeezer.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.List;

import uk.org.ngo.squeezer.R;
import uk.org.ngo.squeezer.Util;
import uk.org.ngo.squeezer.service.ServerString;


public class PlayerState implements Parcelable {

    /**
     * Types of player status subscription.
     */
    public enum PlayerSubscriptionType {
        /** Do not subscribe to updates. */
        none('-'),

        /** Subscribe to updates when the status changes. */
        on_change('0'),

        /** Receive real-time (second to second) updates. */
        real_time('1');

        private char mToken;

        private PlayerSubscriptionType(char token) {
            mToken = token;
        }

        @Override
        public String toString() {
            return String.valueOf(mToken);
        }
    }

    public PlayerState() {
    }

    public static final Creator<PlayerState> CREATOR = new Creator<PlayerState>() {
        @Override
        public PlayerState[] newArray(int size) {
            return new PlayerState[size];
        }

        @Override
        public PlayerState createFromParcel(Parcel source) {
            return new PlayerState(source);
        }
    };

    private PlayerState(Parcel source) {
        playerId = source.readString();
        playStatus = PlayStatus.valueOf(source.readString());
        poweredOn = (source.readByte() == 1);
        shuffleStatus = ShuffleStatus.valueOf(source.readInt());
        repeatStatus = RepeatStatus.valueOf(source.readInt());
        currentSong = source.readParcelable(null);
        currentPlaylist = source.readString();
        currentPlaylistIndex = source.readInt();
        currentTimeSecond = source.readInt();
        currentSongDuration = source.readInt();
        currentVolume = source.readInt();
        sleepDuration = source.readInt();
        sleep = source.readInt();
        mSyncMaster = source.readString();
        source.readStringList(mSyncSlaves);
        mPlayerSubscriptionType = PlayerSubscriptionType.valueOf(source.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerId);
        dest.writeString(playStatus.name());
        dest.writeByte(poweredOn ? (byte) 1 : (byte) 0);
        dest.writeInt(shuffleStatus.getId());
        dest.writeInt(repeatStatus.getId());
        dest.writeParcelable(currentSong, 0);
        dest.writeString(currentPlaylist);
        dest.writeInt(currentPlaylistIndex);
        dest.writeInt(currentTimeSecond);
        dest.writeInt(currentSongDuration);
        dest.writeInt(currentVolume);
        dest.writeInt(sleepDuration);
        dest.writeInt(sleep);
        dest.writeString(mSyncMaster);
        dest.writeStringList(mSyncSlaves);
        dest.writeString(mPlayerSubscriptionType.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private String playerId;

    private boolean poweredOn;

    private PlayStatus playStatus;

    private ShuffleStatus shuffleStatus;

    private RepeatStatus repeatStatus;

    private Song currentSong;

    /** The name of the current playlist, if it has one. */
    private String currentPlaylist;

    private int currentPlaylistIndex;

    private int currentTimeSecond;

    private int currentSongDuration;

    private int currentVolume;

    private int sleepDuration;

    private int sleep;

    /** The player this player is synced to (null if none). */
    @Nullable
    private String mSyncMaster;

    /** The players synced to this player. */
    private ImmutableList<String> mSyncSlaves = new ImmutableList.Builder<String>().build();

    /** How the server is subscribed to the player's status changes. */
    @NonNull
    private PlayerSubscriptionType mPlayerSubscriptionType = PlayerSubscriptionType.none;

    public boolean isPlaying() {
        return playStatus == PlayStatus.play;
    }

    public PlayStatus getPlayStatus() {
        return playStatus;
    }

    public boolean setPlayStatus(PlayStatus state) {
        if (state == playStatus)
            return false;

        playStatus = state;
        return true;
    }

    public boolean setPlayStatus(String s) {
        playStatus = null;
        if (s != null)
            try {
                return setPlayStatus(PlayStatus.valueOf(s));
            } catch (IllegalArgumentException e) {
                // Server sent us an unknown status, nulls are handled outside this function
            }

        return true;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean getPoweredOn() {
        return poweredOn;
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public boolean setPoweredOn(boolean state) {
        if (state == poweredOn)
            return false;

        poweredOn = state;
        return true;
    }

    public ShuffleStatus getShuffleStatus() {
        return shuffleStatus;
    }

    public boolean setShuffleStatus(ShuffleStatus status) {
        if (status == shuffleStatus)
            return false;

        shuffleStatus = status;
        return true;
    }

    public boolean setShuffleStatus(String s) {
        return setShuffleStatus(s != null ? ShuffleStatus.valueOf(Util.parseDecimalIntOrZero(s)) : null);
    }

    public RepeatStatus getRepeatStatus() {
        return repeatStatus;
    }

    public boolean setRepeatStatus(RepeatStatus status) {
        if (status == repeatStatus)
            return false;

        repeatStatus = status;
        return true;
    }

    public boolean setRepeatStatus(String s) {
        return setRepeatStatus(s != null ? RepeatStatus.valueOf(Util.parseDecimalIntOrZero(s)) : null);
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public String getCurrentSongName() {
        return (currentSong != null) ? currentSong.getName() : "";
    }

    public boolean setCurrentSong(Song song) {
        if (song == currentSong)
            return false;

        currentSong = song;
        return true;
    }

    public String getCurrentPlaylist() {
        return currentPlaylist;
    }

    public int getCurrentPlaylistIndex() {
        return currentPlaylistIndex;
    }

    public boolean setCurrentPlaylist(String playlist) {
        if (playlist == null)
            playlist = "";

        if (playlist.equals(currentPlaylist))
            return false;

        currentPlaylist = playlist;
        return true;
    }

    public boolean setCurrentPlaylistIndex(int value) {
        if (value == currentPlaylistIndex)
            return false;

        currentPlaylistIndex = value;
        return true;
    }

    public int getCurrentTimeSecond() {
        return currentTimeSecond;
    }

    public boolean setCurrentTimeSecond(int value) {
        if (value == currentTimeSecond)
            return false;

        currentTimeSecond = value;
        return true;
    }

    public int getCurrentSongDuration() {
        return currentSongDuration;
    }

    public boolean setCurrentSongDuration(int value) {
        if (value == currentSongDuration)
            return false;

        currentSongDuration = value;
        return true;
    }

    public int getCurrentVolume() {
        return currentVolume;
    }

    public boolean setCurrentVolume(int value) {
        if (value == currentVolume)
            return false;

        currentVolume = value;
        return true;
    }

    public int getSleepDuration() {
        return sleepDuration;
    }

    public boolean setSleepDuration(int sleepDuration) {
        if (sleepDuration == this.sleepDuration)
            return false;

        this.sleepDuration = sleepDuration;
        return true;
    }

    /** @return seconds left until the player sleeps. */
    public int getSleep() {
        return sleep;
    }

    /**
     *
     * @param sleep seconds left until the player sleeps.
     * @return True if the sleep value was changed, false otherwise.
     */
    public boolean setSleep(int sleep) {
        if (sleep == this.sleep)
            return false;

        this.sleep = sleep;
        return true;
    }

    public boolean setSyncMaster(@Nullable String syncMaster) {
        if (syncMaster == null && mSyncMaster == null)
            return false;

        if (syncMaster != null) {
            if (syncMaster.equals(mSyncMaster))
                return false;
        }

        mSyncMaster = syncMaster;
        return true;
    }

    @Nullable
    public String getSyncMaster() {
        return mSyncMaster;
    }

    public boolean setSyncSlaves(@NonNull List<String> syncSlaves) {
        if (syncSlaves.equals(mSyncSlaves))
            return false;

        mSyncSlaves = ImmutableList.copyOf(syncSlaves);
        return true;
    }

    public ImmutableList<String> getSyncSlaves() {
        return mSyncSlaves;
    }

    public PlayerSubscriptionType getSubscriptionType() {
        return mPlayerSubscriptionType;
    }

    public boolean setSubscriptionType(@Nullable String type) {
        if (Strings.isNullOrEmpty(type))
            return setSubscriptionType(PlayerSubscriptionType.none);

        switch (type.charAt(0)) {
            case '-':
                return setSubscriptionType(PlayerSubscriptionType.none);

            case '0':
                return setSubscriptionType(PlayerSubscriptionType.on_change);

            case '1':
                return setSubscriptionType(PlayerSubscriptionType.real_time);

            default:
                throw new IllegalArgumentException("Unknown subscription type: " + type);
        }
    }

    public boolean setSubscriptionType(@NonNull PlayerSubscriptionType type) {
        if (type == mPlayerSubscriptionType)
            return false;

        mPlayerSubscriptionType = type;
        return true;
    }

    public static enum PlayStatus {
        play,
        pause,
        stop
    }

    public static enum ShuffleStatus implements EnumWithId {
        SHUFFLE_OFF(0, R.attr.ic_action_av_shuffle_off, ServerString.SHUFFLE_OFF),
        SHUFFLE_SONG(1, R.attr.ic_action_av_shuffle_song, ServerString.SHUFFLE_ON_SONGS),
        SHUFFLE_ALBUM(2, R.attr.ic_action_av_shuffle_album, ServerString.SHUFFLE_ON_ALBUMS);

        private final int id;

        private final int icon;

        private final ServerString text;

        private static final EnumIdLookup<ShuffleStatus> lookup = new EnumIdLookup<ShuffleStatus>(
                ShuffleStatus.class);

        private ShuffleStatus(int id, int icon, ServerString text) {
            this.id = id;
            this.icon = icon;
            this.text = text;
        }

        @Override
        public int getId() {
            return id;
        }

        public int getIcon() {
            return icon;
        }

        public ServerString getText() {
            return text;
        }

        public static ShuffleStatus valueOf(int id) {
            return lookup.get(id);
        }
    }

    public static enum RepeatStatus implements EnumWithId {
        REPEAT_OFF(0, R.attr.ic_action_av_repeat_off, ServerString.REPEAT_OFF),
        REPEAT_ONE(1, R.attr.ic_action_av_repeat_one, ServerString.REPEAT_ONE),
        REPEAT_ALL(2, R.attr.ic_action_av_repeat_all, ServerString.REPEAT_ALL);

        private final int id;

        private final int icon;

        private final ServerString text;

        private static final EnumIdLookup<RepeatStatus> lookup = new EnumIdLookup<RepeatStatus>(
                RepeatStatus.class);

        private RepeatStatus(int id, int icon, ServerString text) {
            this.id = id;
            this.icon = icon;
            this.text = text;
        }

        @Override
        public int getId() {
            return id;
        }

        public int getIcon() {
            return icon;
        }

        public ServerString getText() {
            return text;
        }

        public static RepeatStatus valueOf(int id) {
            return lookup.get(id);
        }
    }

    public interface EnumWithId {

        int getId();
    }

    public static class EnumIdLookup<E extends Enum<E> & EnumWithId> {

        private final SparseArray<E> map = new SparseArray<E>();

        public EnumIdLookup(Class<E> enumType) {
            for (E v : enumType.getEnumConstants()) {
                map.put(v.getId(), v);
            }
        }

        public E get(int num) {
            return map.get(num);
        }
    }

}
