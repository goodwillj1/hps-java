void simple() {

    TH1 *h=(TH1*)((TDirectoryFile*)gDirectory->Get("mu+"))->Get("sumEnergy2");

    TF1 *f=new TF1("f",langaufun,0.0,0.3,4);

    f->SetParameters(0.001,0.17,100,0.01);

    f->SetRange(0.1,0.3);

    h->Fit(f,"R");

}

