(
SynthDef.new(name:\simple, ugenGraphFunc:{
    | out, freq = 440|
    var source = SinOsc.ar(freq) * Env.perc(0.1,0.3,curve:-8).ar(doneAction: Done.freeSelf);
    Out.ar(bus:out, channelsArray:source)
}, rates:nil, prependArgs:nil, variants:nil, metadata:nil).add;
)

FiniteStateMachine {
    var states, transitionMatrix, currentState;
    *new { |states, jk

    }
    getNext() {
    }
}



s = Routine({
    var pitchStates = [440, 880];
    var stateCount = pitchStates.size;
    var pitchMat = [
        [0.1,0.9],
        [1,0]
    ];
    var currentPitchState = 0;
    var delta = 1/4;
    var indexes = Array.series(stateCount,0,1);
    loop {
        Synth(\simple, [\freq, pitchStates[currentPitchState]]);
        currentPitchState = indexes.wchoose(pitchMat[currentPitchState]);
        currentPitchState.postln;
        delta.yield;
    };
});
s.play;
s.stop;
