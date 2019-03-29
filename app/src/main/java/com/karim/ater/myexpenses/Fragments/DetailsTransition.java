package com.karim.ater.myexpenses.Fragments;

import androidx.transition.ChangeBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.TransitionSet;

public class DetailsTransition extends TransitionSet {
    public DetailsTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds().setDuration(600L)).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
